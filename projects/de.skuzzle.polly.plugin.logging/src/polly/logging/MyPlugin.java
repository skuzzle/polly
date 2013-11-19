package polly.logging;


import http.AllDayFilter;
import http.LogEntryTableModel;
import http.LoggingController;
import http.ReplayTableModel;

import java.io.IOException;

import commands.ChannelLogCommand;
import commands.ReplayCommand;
import commands.SeenCommand;
import commands.UserLogCommand;

import core.ForwardHighlightHandler;
import core.IrcLogCollector;
import core.PollyLoggingManager;
import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.ConfigurationProvider;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.constraints.Constraints;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.httpv2.MenuCategory;
import de.skuzzle.polly.sdk.httpv2.html.HTMLTable;
import de.skuzzle.polly.sdk.httpv2.html.HTMLTableModel;
import de.skuzzle.polly.sdk.roles.RoleManager;
import entities.LogEntry;



public class MyPlugin extends PollyPlugin {

    public final static String LOGGING_ROLE           = "polly.roles.LOGGING"; //$NON-NLS-1$
    public final static String USER_LOG_PERMISSION    = "polly.permission.USER_LOG"; //$NON-NLS-1$
    public final static String CHANNEL_LOG_PERMISSION = "polly.permission.CHANNEL_LOG"; //$NON-NLS-1$
    public final static String REPLAY_PERMISSION      = "polly.permission.REPLAY"; //$NON-NLS-1$
    public final static String SEEN_PERMISSION        = "polly.permission.SEEN"; //$NON-NLS-1$
    
    
    public final static String LOGGING_PLUGUIN_CFG = "plugin.logging.cfg"; //$NON-NLS-1$
    
    public final static String LOG_CACHE_SIZE     = "logCacheSize"; //$NON-NLS-1$
    public final static String LOG_PASTE_TRESHOLD = "logPasteThreshold"; //$NON-NLS-1$
    public final static String LOG_MAX_LOGS       = "logMaxLogs"; //$NON-NLS-1$
    
    public final static int DEFAULT_LOG_CACHE_SIZE  = 100;
    public final static int DEFAULT_LOG_THRESHOLD   = 10;
    public final static int DEFAULT_MAX_LOGS        = 100;
    
    public final static String FORWARD_HIGHLIGHTS         = "FORWARD_HIGHLIGHTS"; //$NON-NLS-1$
    public final static Types DEFAULT_FORWARD_HIGHLIGHTS  = new Types.BooleanType(false);
    
    private IrcLogCollector logCollector;
    private PollyLoggingManager logManager;
    private MessageListener highlightForwarder;
    
    
    public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException, 
                DuplicatedSignatureException {
        
        super(myPolly);

        myPolly.persistence().registerEntity(LogEntry.class);
        
        ConfigurationProvider cfgProvider = myPolly.configuration();
        
        Configuration loggingCfg = null;
        try {
            loggingCfg = cfgProvider.open(LOGGING_PLUGUIN_CFG);
        } catch (IOException e) {
            loggingCfg = cfgProvider.emptyConfiguration();
        }
        
        int cacheSize = loggingCfg.readInt(
                LOG_CACHE_SIZE, DEFAULT_LOG_CACHE_SIZE);
        int pasteTreshold = loggingCfg.readInt(
            LOG_PASTE_TRESHOLD, DEFAULT_LOG_THRESHOLD);
        int maxLogs = loggingCfg.readInt(LOG_MAX_LOGS, DEFAULT_MAX_LOGS);
        

        this.logManager = new PollyLoggingManager(
            myPolly, cacheSize, pasteTreshold, maxLogs);
        
        this.logCollector = new IrcLogCollector(this.logManager);
        
        this.addDisposable(this.logManager);
        
        myPolly.irc().addJoinPartListener(this.logCollector);
        myPolly.irc().addMessageListener(this.logCollector);
        myPolly.irc().addQuitListener(this.logCollector);
        myPolly.irc().addNickChangeListener(this.logCollector);
        
        this.addCommand(new UserLogCommand(myPolly, this.logManager));
        this.addCommand(new ChannelLogCommand(myPolly, this.logManager));
        this.addCommand(new SeenCommand(myPolly, this.logManager));
        this.addCommand(new ReplayCommand(myPolly, this.logManager));
        
        this.highlightForwarder = new ForwardHighlightHandler(myPolly, this.logManager);
        myPolly.irc().addMessageListener(this.highlightForwarder);
        
        
        myPolly.webInterface().addCategory(new MenuCategory(1, MSG.httpLoggingCategory));
        myPolly.webInterface().getServer().addController(new LoggingController(myPolly, logManager));
        
        final HTMLTableModel<LogEntry> model = new LogEntryTableModel(logManager, myPolly);
        final HTMLTableModel<LogEntry> replayModel = new ReplayTableModel(logManager, myPolly);
        
        final HTMLTable<LogEntry> logTable = new HTMLTable<LogEntry>("allLogs", model, myPolly); //$NON-NLS-1$
        final HTMLTable<LogEntry> replayTable = new HTMLTable<LogEntry>("replay", replayModel, myPolly); //$NON-NLS-1$
        
        logTable.setFilter(new AllDayFilter(myPolly));
        replayTable.setFilter(new AllDayFilter(myPolly));
        
        myPolly.webInterface().getServer().addHttpEventHandler("/api/allLogs", logTable); //$NON-NLS-1$
        myPolly.webInterface().getServer().addHttpEventHandler("/api/replay", replayTable); //$NON-NLS-1$
    }
    
    
    
    @Override
    public void onLoad() {
        try {
            this.getMyPolly().users().addAttribute(FORWARD_HIGHLIGHTS, 
                DEFAULT_FORWARD_HIGHLIGHTS, 
                MSG.forwardHLDesc, 
                MSG.loggingAttributeCategory, Constraints.BOOLEAN);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }
    
    
    
    @Override
    public void assignPermissions(RoleManager roleManager)
            throws RoleException, DatabaseException {
        
        roleManager.createRole(LOGGING_ROLE);
        roleManager.assignPermission(LOGGING_ROLE, USER_LOG_PERMISSION);
        roleManager.assignPermission(LOGGING_ROLE, CHANNEL_LOG_PERMISSION);
        roleManager.assignPermission(LOGGING_ROLE, REPLAY_PERMISSION);
        roleManager.assignPermission(LOGGING_ROLE, SEEN_PERMISSION);
        
        super.assignPermissions(roleManager);
    }
    
    
    
    @Override
    protected void actualDispose() throws DisposingException {
        super.actualDispose();
        
        this.getMyPolly().irc().removeJoinPartListener(this.logCollector);
        this.getMyPolly().irc().removeMessageListener(this.logCollector);
        this.getMyPolly().irc().removeQuitListener(this.logCollector);
        this.getMyPolly().irc().removeNickChangeListener(this.logCollector);
        this.getMyPolly().irc().removeMessageListener(this.highlightForwarder);
    }
}
