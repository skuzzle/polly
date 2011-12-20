package polly.core.irc;

import java.util.concurrent.ExecutorService;

import org.jibble.pircbot.NickAlreadyInUseException;

import polly.configuration.PollyConfiguration;
import polly.core.ShutdownManagerImpl;
import polly.core.commands.CommandManagerImpl;
import polly.core.users.UserManagerImpl;
import polly.eventhandler.AutoLogonLogoffHandler;
import polly.eventhandler.IrcConnectionLostListener;
import polly.eventhandler.IrcLoggingHandler;
import polly.eventhandler.MessageHandler;
import polly.eventhandler.TraceNickChangeHandler;
import polly.events.EventProvider;
import polly.util.AbstractPollyModule;
import polly.util.ModuleBlackboard;



public class IrcModule extends AbstractPollyModule {

    private EventProvider events;
    private ExecutorService commandExecutor;
    private CommandManagerImpl commandManager;
    private UserManagerImpl userManager;
    private IrcManagerImpl ircManager;
    private PollyConfiguration config;
    private ShutdownManagerImpl shutdownManager;
    
    private BotConnectionSettings connectionSettings;
    
    public IrcModule(ModuleBlackboard initializer) {
        super("IRC", initializer);
    }

    
    
    @Override
    public void require() {
        this.config = this.requireComponent(PollyConfiguration.class);
        this.events = this.requireComponent(EventProvider.class);
        this.commandExecutor = this.requireComponent(ExecutorService.class);
        this.userManager = this.requireComponent(UserManagerImpl.class);
        this.commandManager = this.requireComponent(CommandManagerImpl.class);
        this.shutdownManager = this.requireComponent(ShutdownManagerImpl.class);
    }
    
    
    
    @Override
    public boolean doSetup() throws Exception {
        this.ircManager = new IrcManagerImpl(
            this.config.getNickName(), 
            this.events, 
            this.config);
        
        this.provideComponent(IrcManagerImpl.class, this.ircManager);
        
        logger.info("Starting bot with settings: (" +
            "Nick: " + this.config.getNickName() + 
            ", Ident: *****" + 
            ", Server: " + this.config.getServer() + 
            ", Port: " + this.config.getPort() + 
            ", Logging: " + this.config.getIrcLogging() + ")");
        
        
        // setup irc logger
        if (this.config.getIrcLogging()) {
            IrcLoggingHandler ircConsoleLogger = new IrcLoggingHandler();
            this.ircManager.addMessageListener(ircConsoleLogger);
            this.ircManager.addNickChangeListener(ircConsoleLogger);
            this.ircManager.addJoinPartListener(ircConsoleLogger);
        }
        
        // setup handler for incoming irc messages
        MessageHandler handler = new MessageHandler(
            this.commandManager, 
            this.userManager, 
            this.config.getEncodingName(), 
            this.commandExecutor);
        this.ircManager.addMessageListener(handler);
        this.provideComponent(MessageHandler.class, handler);
        
        this.ircManager.addNickChangeListener(new TraceNickChangeHandler(this.userManager));
        this.ircManager.addConnectionListener(new IrcConnectionLostListener(this.userManager));
        
        
        // Setup auto logoin / logout handler
        AutoLogonLogoffHandler logonHandler = new AutoLogonLogoffHandler(
            this.ircManager, this.userManager, this.config);
        
        this.ircManager.addUserSpottedListener(logonHandler);
        this.ircManager.addNickChangeListener(logonHandler);
        this.userManager.addUserListener(logonHandler);
        
        this.shutdownManager.addDisposable(logonHandler);
        
        this.connectionSettings = new BotConnectionSettings(
            this.config.getNickName(), 
            this.config.getServer(), 
            this.config.getPort(), 
            this.config.getIdent(),
            this.config.getChannels(),
            this.config.getIrcModes());
        
        
        this.shutdownManager.addDisposable(this.ircManager);
        return true;
    }

    
    
    @Override
    public void doRun() throws Exception {
        try {
            this.ircManager.connect(this.connectionSettings);
        } catch (NickAlreadyInUseException e) {
            logger.fatal("Connection rejected: nickname in use.", e);
            throw e;
        } catch (Exception e) {
            logger.fatal("Connection failed: " + e.getMessage(), e);
            throw e;
        }
    }

}