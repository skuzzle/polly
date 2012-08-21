package polly.porat.core;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;


import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.model.User;
import de.skuzzle.polly.sdk.roles.RoleManager;

import polly.porat.core.tcp.ServerConnection;

import polly.network.Connection;
import polly.network.protocol.Constants;
import polly.network.protocol.Constants.ResponseType;
import polly.network.protocol.LogItem;
import polly.network.protocol.Response;
import polly.network.util.SerializableFile;



public class AdministrationManager extends AbstractDisposable {
    
    public enum LoginResult {
        SUCCESS, INSUFICCIENT_RIGHTS, UNKNOWN_USER, INVALID_PASSWORD;
    }
    
    
    // TODO: move this to config file
    public final static int LIVE_LOG_THRESHOLD = 50;
    
    private final static File LOG_DIR = new File("./logs");
    private final static Pattern LOG_PATTERN = Pattern.compile(".+\\.log(\\.\\d+)?$");
    private final static Logger logger = Logger.getLogger(
            AdministrationManager.class.getName());
    
    private Set<Connection> liveLogList;
    private Set<Connection> ircForwards;
    private ExecutorService sender;
    private CachedLogAppender logAppender;
    private UserManager userManager;
    private RoleManager roleManager;
    
    
    public AdministrationManager(UserManager userManager, RoleManager roleManager) {
        this.userManager = userManager;
        this.roleManager = roleManager;
        this.sender = Executors.newFixedThreadPool(1);
        this.liveLogList = new HashSet<Connection>();
        this.ircForwards = new HashSet<Connection>();
        
        this.logAppender = new CachedLogAppender(this, LIVE_LOG_THRESHOLD);
        Logger.getRootLogger().addAppender(this.logAppender);
    }
    
    
    
    public CachedLogAppender getLogAppender() {
        return this.logAppender;
    }
    
    
    
    public LoginResult login(Connection connection, String userName, String password) {
        User user = this.userManager.getUser(userName);
        
        if (user == null) {
            return LoginResult.UNKNOWN_USER;
        } else if (!user.checkPassword(password)) {
            return LoginResult.INVALID_PASSWORD;
        } else if (!this.roleManager.hasPermission(user, RoleManager.ADMIN_PERMISSION)) {
            return LoginResult.INSUFICCIENT_RIGHTS;
        } else {
            ((ServerConnection) connection).setUser(user);
            return LoginResult.SUCCESS;
        }
        
    }
    
    
    
    public void logout(Connection connection) {
        ((ServerConnection) connection).setUser(null);
    }
    
    
    
    public void enableIrcForward(Connection connection) {
        synchronized (this.ircForwards) {
            this.ircForwards.add(connection);
            logger.info("IRC forward enabled for " + connection);
        }
    }
    
    
    
    public void disableIrcForward(Connection connection) {
        synchronized (this.ircForwards) {
            this.ircForwards.remove(connection);
            logger.info("IRC forward disabled for " + connection);
        }
    }
    
    
    
    public void enableLiveLog(Connection connection) {
        synchronized (this.liveLogList) {
            this.liveLogList.add(connection);
            this.logAppender.setEnabled(!this.liveLogList.isEmpty());
            logger.info("Live-Log enabled for " + connection);
        }
    }
    
    
    
    public void disableLiveLog(Connection connection) {
        synchronized (this.liveLogList) {
            this.liveLogList.remove(connection);
            this.logAppender.setEnabled(!this.liveLogList.isEmpty());
            logger.info("Live-Log disabled for " + connection);
        }
    }
    
    
    
    public void sendLogs(Connection connection) {
        logger.info("Connection " + connection + " requested logfiles");
        Response response = new Response(ResponseType.FILE);
        response.getPayload().put(Constants.LOG_LIST, this.listLogFiles());

        ((ServerConnection) connection).send(response, true);
        logger.info("Logfiles sent");
    }
    
    
    
    private List<SerializableFile> listLogFiles() {
        logger.trace("Listing logfiles...");
        File[] logs = LOG_DIR.listFiles(new FileFilter() {
            
            @Override
            public boolean accept(File file) {
                String name = file.getName();
                return LOG_PATTERN.matcher(name).matches(); 
            }
        });
        logger.trace("Found " + logs.length + " Logfiles to send.");
        List<SerializableFile> result = new ArrayList<SerializableFile>(logs.length);
        for (File file : logs) {
            result.add(new SerializableFile(file));
        }
        return result;
    }
    
    
    
    public void processLogCache(List<LogItem> cache, boolean asynchron) {
        final List<LogItem> copy; 
        synchronized (cache) {
            copy = new ArrayList<LogItem>(cache);
            cache.clear();
        }
        
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Response response = new Response(ResponseType.LOG_ITEM);
                response.getPayload().put(Constants.LOG_LIST, copy);
                
                synchronized (liveLogList) {
                    for (Connection connection : liveLogList) {
                        connection.send(response);
                    }
                }
            }
        };
        if (asynchron) {
            this.sender.equals(r);
        } else {
            r.run();
        }
    }



    @Override
    protected void actualDispose() throws DisposingException {
        this.logAppender.setEnabled(false);
        this.logAppender.processLogCacheNow();
        Logger.getRootLogger().removeAppender(this.logAppender);
        try {
            this.sender.awaitTermination(1000, TimeUnit.MILLISECONDS);
            this.sender.shutdown();
        } catch (InterruptedException ignore) {
            ignore.printStackTrace();
        }
        this.liveLogList.clear();
    }

}
