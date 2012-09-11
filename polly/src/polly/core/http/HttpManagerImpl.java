package polly.core.http;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;


import com.sun.net.httpserver.HttpServer;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpEventListener;
import de.skuzzle.polly.sdk.http.HttpManager;
import de.skuzzle.polly.sdk.http.HttpSession;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;
import de.skuzzle.polly.sdk.model.User;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.tools.concurrent.ThreadFactoryBuilder;
import de.skuzzle.polly.tools.events.Dispatchable;
import de.skuzzle.polly.tools.events.EventProvider;
import de.skuzzle.polly.tools.events.SynchronousEventProvider;



public class HttpManagerImpl extends AbstractDisposable implements HttpManager {
    
    
    private final static Logger logger = Logger.getLogger(HttpManagerImpl.class
        .getName());
    
    
    public final static String FILE_REQUEST_PREFIX = "/file:";
    
    
    
    private HttpServer server;
    private int port;
    private boolean running;
    private Map<InetAddress, HttpSession> sessions;
    private EventProvider eventProvider;
    private Map<String, HttpAction> actions;
    private Map<String, List<String>> menu;
    private File templateRoot;
    private int sessionTimeOut;
    private RoleManager roleManager;
    private MyPolly myPolly;
    private String publicHost;
    private String encoding;
    private int cacheCounter;
    private int cacheThreshold;
    private int errorThreshold;
    private TrafficCounter counter;
    private List<HttpSession> expiredSessions;
    
    
    
    public HttpManagerImpl(File templateRoot, String publicHost,
            int port, int sessionTimeOut, String encoding, 
            int cacheTreshold, int errorThreshold) {
        
        this.templateRoot = templateRoot;
        this.publicHost = publicHost;
        this.port = port;
        this.encoding = encoding;
        this.sessionTimeOut = sessionTimeOut;
        this.sessions = new HashMap<InetAddress, HttpSession>();
        this.eventProvider = new SynchronousEventProvider();
        this.actions = new HashMap<String, HttpAction>();
        this.menu = new TreeMap<String, List<String>>();
        this.cacheThreshold = cacheTreshold;
        this.errorThreshold = errorThreshold;
        this.counter = new TrafficCounter();
        this.expiredSessions = new LinkedList<HttpSession>();
    }
    
    
    
    public void setMyPolly(MyPolly myPolly) {
        // HACK: need this setter to avoid cyclic dependency
        this.myPolly = myPolly;
        this.roleManager = myPolly.roles();
    }
    
    
    
    public int getErrorThreshold() {
        return this.errorThreshold;
    }
    
    
    
    public File getTemplateRoot() {
        return this.templateRoot;
    }
    
    
    
    @Override
    public String getPublicHost() {
        return this.publicHost;
    }
    
    
    
    @Override
    public int getPort() {
        return this.port;
    }
    
    
    
    public TrafficCounter getTrafficCounter() {
        return this.counter;
    }
    
    
    
    @Override
    public void startWebServer() throws IOException {
        if (this.isRunning()) {
            return;
        }
        logger.info("Starting webserver at port " + this.port);
        this.server = HttpServer.create(new InetSocketAddress(this.port), 5);
        
        // this context handles requests for files
        this.server.createContext(FILE_REQUEST_PREFIX, 
            new FileResponseHandler(this, this.counter, FILE_REQUEST_PREFIX));
        
        // create action handler and add a filter which filters file requests
        this.server.createContext(
            "/", new ActionResponseHandler(
                this, this.counter)).getFilters().add(
                    new FileFilter(FILE_REQUEST_PREFIX));
        
        this.server.setExecutor(
            Executors.newCachedThreadPool(
                new ThreadFactoryBuilder("HTTP_SERVER_%n%")));
        this.server.start();
        this.running = true;
        logger.info("Webserver running.");
    }
    


    @Override
    public void stopWebServer() {
        logger.info("Stopping http service");
        if (this.server != null) {
            this.server.stop(5);
        }
        synchronized (this.sessions) {
            this.sessions.clear();
        }
        this.running = false;
        this.server = null;
    }
    
    
    
    protected HttpSession getSession(InetAddress remoteIp) {
        synchronized (this.sessions) {
            HttpSession session = this.sessions.get(remoteIp);
            if (session == null) {
                session = new HttpSession(generateSessionId(remoteIp), remoteIp);
                this.sessions.put(remoteIp, session);
            }
            return session;
        }
    }
    
    
    
    @Override
    public HttpSession findSession(String id) {
        for (HttpSession session : this.sessions.values()) {
            if (session.getId().equalsIgnoreCase(id)) {
                return session;
            }
        }
        return null;
    }
    
    
    
    public void closeSession(HttpSession session) {
        synchronized (this.sessions) {
            HttpSession copy = session.copy();
            session.setUser(null);
            this.sessions.remove(session.getRemoteIp());
            this.expiredSessions.add(copy);
        }
    };
    
    
    
    @Override
    public void cleanUpSessions() {
        synchronized (this.sessions) {
            if (++this.cacheCounter % this.cacheThreshold == 0) {
                logger.debug("Cleaning session cache...");
                Iterator<Entry<InetAddress, HttpSession>> it = 
                        this.sessions.entrySet().iterator();
                        
                while (it.hasNext()) {
                    Entry<InetAddress, HttpSession> e = it.next();
                    long liveTime = 
                            System.currentTimeMillis() - e.getValue().getLastAction();
                    boolean timedOut = liveTime > this.getSessionTimeOut();
                    
                    if (timedOut) {
                        logger.warn("Killing " + e.getValue());
                        e.getValue().setUser(null);
                        try {
                            e.getValue().dispose();
                        } catch (DisposingException e1) {
                            // Impossibru to happen
                            logger.fatal(e1);
                        }
                        it.remove();
                    }
                }
            }
        }
    }
    
    
    
    private final static Random RANDOM = new Random();
    
    private static String generateSessionId(InetAddress remoteIp) {
        long id = RANDOM.nextLong() * System.currentTimeMillis() * remoteIp.hashCode();
        return Long.toHexString(id);
    }
    
    
    
    public boolean isRunning() {
        return this.running;
    }
    
    
    
    @Override
    public HttpTemplateContext executeAction(HttpEvent e) 
                throws HttpTemplateException, InsufficientRightsException {
        String uri = e.getRequestUri();
        HttpAction action = this.actions.get(uri);
        
        HttpTemplateContext actionContext = null;
        if (action == null) {
            return null;
        } else {
            if (this.roleManager.canAccess(e.getSession().getUser(), action)) {
                actionContext = action.execute(e);
                actionContext.put(HttpInterface.PERMISSIONS, 
                        action.getRequiredPermission());
            } else {
                throw new InsufficientRightsException(action);
            }
        }
        this.putRootContext(actionContext, e.getSession());
        actionContext.put(HttpInterface.CONTENT, 
                this.getPage(actionContext.getTemplate()).getPath());
        
        return actionContext;
    }
    
    
    
    @Override
    public HttpTemplateContext errorTemplate(String errorHeading, 
            String errorDescription, HttpSession session) {
        HttpTemplateContext c = new HttpTemplateContext(HttpInterface.PAGE_ERROR);
        c.put(HttpInterface.ERROR_HEADING, errorHeading);
        c.put(HttpInterface.ERROR_DESCRIPTION, errorDescription);
        c.put(HttpInterface.CONTENT, this.getPage(HttpInterface.PAGE_ERROR).getPath());
        c.put(HttpInterface.PERMISSIONS, 
                Collections.singleton(RoleManager.NONE_PERMISSION));
        this.putRootContext(c, session);
        return c;
    }
    
    
    
    @Override
    public File getPage(String name) {
        return new File(this.templateRoot, name);
    }
    
    
    
    @Override
    public void addHttpAction(HttpAction action) {
        this.actions.put(action.getName(), action);
    }
    
    
    
    protected void putRootContext(HttpTemplateContext c, HttpSession session) {
        c.put("menu", this.menu);
        c.put("title", "Polly Webinterface");
        c.put("heading", "Polly Webinterface");
        c.put("me", session.getUser());
        c.put("myPolly", this.myPolly);
        c.put("session", session);
        c.put("now", System.currentTimeMillis());
        c.put("timeout", this.getSessionTimeOut());
        c.put("errorThreshold", this.errorThreshold);
        c.put("sessions", this.sessions);
        c.put("expiredSessions", this.expiredSessions);
        c.put("traffic", this.counter);
        c.put("Math", Math.class);
    }
    
    
    
    public boolean actionExists(String action) {
        return this.actions.containsKey(action);
    }


    
    @Override
    public void addMenuUrl(String category, String name) {
        List<String> entries = this.menu.get(category);
        if (entries == null) {
            entries = new ArrayList<String>();
            this.menu.put(category, entries);
        }
        entries.add(name);
    }
    
    
    
    @Override
    public void removeMenuUrl(String category, String name) {
        if (this.menu.containsKey(category)) {
            this.menu.get(category).remove(name);
        }
    }
    
    
    
    protected void fireHttpAction(final HttpEvent e) {
        final List<HttpEventListener> listeners = 
            this.eventProvider.getListeners(HttpEventListener.class);
        
        this.eventProvider.dispatchEvent(new Dispatchable<HttpEventListener, 
                    HttpEvent>(listeners, e) {
            @Override
            public void dispatch(HttpEventListener listener, HttpEvent event) {
                // Check if url matches the url pattern of the listener
                if (event.getRequestUri().matches(listener.getActionUrl())) {
                    listener.httpAction(e);
                }
            }
        });
    }



    @Override
    public void addHttpEventListener(HttpEventListener listener) {
        this.eventProvider.addListener(HttpEventListener.class, listener);
    }

    
    
    @Override
    public void removeHttpEventListener(HttpEventListener listener) {
        this.eventProvider.addListener(HttpEventListener.class, listener);
    }



    public int getSessionTimeOut() {
        return this.sessionTimeOut;
    }



    @Override
    protected void actualDispose() throws DisposingException {
        this.stopWebServer();
    }



    public String getEncoding() {
        return this.encoding;
    }
    
    
    
    @Override
    public String escapeHtml(String s) {
        return StringEscapeUtils.escapeHtml(s);
    }
    
    
    
    @Override
    public String makeActionLink(String actionName, User user, 
            String prefix, String postfix) {
        String tmp = actionName.startsWith("/") ? actionName : "/" + actionName;
        
        HttpAction action = this.actions.get(tmp);
        if (action == null) {
            return "";
        } else if (!this.myPolly.roles().canAccess(user, action)) {
            return "";
        }
        
        return prefix + "<a href=\"" + actionName + "\">" + actionName + "</a>" + postfix;
    }
}