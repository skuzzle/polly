package polly.core.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import polly.events.Dispatchable;
import polly.events.EventProvider;
import polly.events.SynchronousEventProvider;
import polly.util.concurrent.ThreadFactoryBuilder;

import com.sun.net.httpserver.HttpServer;

import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpEventListener;
import de.skuzzle.polly.sdk.http.HttpManager;
import de.skuzzle.polly.sdk.http.HttpSession;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;



public class SimpleWebServer implements HttpManager {
    
    
    private final static Logger logger = Logger.getLogger(SimpleWebServer.class
        .getName());
    
    
    
    /**
     * Timeout after which a session is considered invalid
     */
    public final static int SESSION_TIMEOUT = 10 * 1000 * 60;
    
    
    private HttpServer server;
    private int port;
    private boolean running;
    private Map<InetAddress, HttpSession> sessions;
    private EventProvider eventProvider;
    private Map<String, HttpAction> actions;
    private ArrayList<String> menu;
    
    
    
    public SimpleWebServer(int port) {
        this.port = port;
        this.sessions = new HashMap<InetAddress, HttpSession>();
        this.eventProvider = new SynchronousEventProvider();
        this.actions = new HashMap<String, HttpAction>();
        this.menu = new ArrayList<String>();
    }
    
    
    
    public void startServer() throws IOException {
        if (this.isRunning()) {
            return;
        }
        logger.info("Starting webserver at port " + this.port);
        this.server = HttpServer.create(new InetSocketAddress(this.port), 5);
        this.server.createContext("/", new ResponseHandler(this));
        this.server.setExecutor(
            Executors.newSingleThreadExecutor(
                new ThreadFactoryBuilder("HTTP_SERVER")));
        this.server.start();
        this.running = true;
        logger.info("Webserver running.");
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
    
    
    
    protected void closeSession(HttpSession session) {
        synchronized (this.sessions) {
            logger.warn("Killing " + session);
            this.sessions.remove(session.getRemoteIp());
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
    
    
    
    protected HttpTemplateContext executeAction(HttpEvent e) {
        HttpAction action = this.actions.get(e.getRequestUri());
        
        HttpTemplateContext actionContext = new HttpTemplateContext();
        if (action == null) {
            actionContext.setTemplate("webinterface/pages/error.html");
            actionContext.put("errorText", "No such action.");
            return null;
        } else {
            action.execute(e, actionContext);
        }
        this.putRootContext(actionContext, e.getSession());
        actionContext.put("content", actionContext.getTemplate());
        
        return actionContext;
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
    }


    
    @Override
    public void addMenuUrl(String name) {
        this.menu.add(name);
    }
    
    
    
    @Override
    public void removeMenuUrl(String name) {
        this.menu.remove(name);
    }
    
    
    
    @Override
    public void startWebServer() {
    }



    @Override
    public void stopWebServer() {
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
}