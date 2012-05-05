package polly.core.irc;

import java.util.concurrent.ExecutorService;

import org.jibble.pircbot.NickAlreadyInUseException;

import polly.configuration.PollyConfiguration;
import polly.core.DefaultUserAttributes;
import polly.core.ShutdownManagerImpl;
import polly.core.commands.CommandManagerImpl;
import polly.core.users.UserManagerImpl;
import polly.eventhandler.AutoLogoffHandler;
import polly.eventhandler.AutoLogonHandler;
import polly.eventhandler.GhostHandler;
import polly.eventhandler.IrcLoggingHandler;
import polly.eventhandler.MessageHandler;
import polly.eventhandler.TraceNickChangeHandler;
import polly.events.EventProvider;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;
import polly.moduleloader.annotations.Provide;
import polly.core.ModuleStates;


@Module(
    requires = { 
        @Require(component = PollyConfiguration.class),
        @Require(component = ShutdownManagerImpl.class),
        @Require(component = EventProvider.class),
        @Require(component = UserManagerImpl.class),
        @Require(component = CommandManagerImpl.class),
        @Require(component = ExecutorService.class),
        @Require(component = DefaultUserAttributes.class),
        @Require(state = ModuleStates.PLUGINS_READY),
        @Require(state = ModuleStates.PERSISTENCE_READY) }, 
    provides = {
        @Provide(component = IrcManagerImpl.class),
        @Provide(component = MessageHandler.class),
        @Provide(state = ModuleStates.IRC_READY) })
public class IrcModule extends AbstractModule {

    private EventProvider events;
    private ExecutorService commandExecutor;
    private CommandManagerImpl commandManager;
    private UserManagerImpl userManager;
    private IrcManagerImpl ircManager;
    private PollyConfiguration config;
    private ShutdownManagerImpl shutdownManager;

    private BotConnectionSettings connectionSettings;



    public IrcModule(ModuleLoader loader) {
        super("MODULE_IRC", loader, true);
    }



    @Override
    public void beforeSetup() {
        this.config = this.requireNow(PollyConfiguration.class);
        this.events = this.requireNow(EventProvider.class);
        this.userManager = this.requireNow(UserManagerImpl.class);
        this.commandManager = this.requireNow(CommandManagerImpl.class);
        this.shutdownManager = this.requireNow(ShutdownManagerImpl.class);
        this.commandExecutor = this.requireNow(ExecutorService.class);
    }



    @Override
    public void setup() throws SetupException {
        this.ircManager = new IrcManagerImpl(this.config.getNickName(),
            this.events, this.config);

        this.provideComponent(this.ircManager);

        logger.info("Starting bot with settings: (" + "Nick: "
            + this.config.getNickName() + ", Ident: *****" + ", Server: "
            + this.config.getServer() + ", Port: " + this.config.getPort()
            + ", Logging: " + this.config.getIrcLogging() + ")");

        // setup irc logger
        if (this.config.getIrcLogging()) {
            IrcLoggingHandler ircConsoleLogger = new IrcLoggingHandler();
            this.ircManager.addMessageListener(ircConsoleLogger);
            this.ircManager.addNickChangeListener(ircConsoleLogger);
            this.ircManager.addJoinPartListener(ircConsoleLogger);
        }

        // setup handler for incoming irc messages
        MessageHandler handler = new MessageHandler(this.commandManager,
            this.userManager, this.commandExecutor, this.config);
        this.ircManager.addMessageListener(handler);
        this.provideComponent(handler);
        
        // this listener checks if we ghosted our original nick and changes our nickname
        // to the default one
        this.ircManager.addMessageListener(new GhostHandler());

        this.ircManager.addNickChangeListener(new TraceNickChangeHandler(
            this.userManager));
        

        // Setup auto login / logout handler
        if (this.config.isAutoLogon()) {
            AutoLogonHandler logonHandler = new AutoLogonHandler(
                this.ircManager, this.userManager, 
                this.config.getAutoLogonTime());

            this.ircManager.addUserSpottedListener(logonHandler);
            this.ircManager.addNickChangeListener(logonHandler);
            this.ircManager.addConnectionListener(logonHandler);
            this.userManager.addUserListener(logonHandler);

            this.shutdownManager.addDisposable(logonHandler);
        }
        AutoLogoffHandler logoffHandler = new AutoLogoffHandler(this.userManager, 
            this.ircManager);
        this.ircManager.addUserSpottedListener(logoffHandler);
        this.ircManager.addConnectionListener(logoffHandler);
            
        this.connectionSettings = new BotConnectionSettings(
            this.config.getNickName(), this.config.getServer(),
            this.config.getPort(), this.config.getIdent(),
            this.config.getChannels(), this.config.getIrcModes());

        this.shutdownManager.addDisposable(this.ircManager);
    }



    @Override
    public void run() throws Exception {
        try {
            this.ircManager.connect(this.connectionSettings);
            this.addState(ModuleStates.IRC_READY);
        } catch (NickAlreadyInUseException e) {
            logger.fatal("Connection rejected: nickname in use.", e);
            throw e;
        } catch (Exception e) {
            logger.fatal("Connection failed: " + e.getMessage(), e);
            throw e;
        }
    }
    
    
    
    @Override
    public void dispose() {
        this.config = null;
        this.events = null;
        this.userManager = null;
        this.commandManager = null;
        this.shutdownManager = null;
        this.commandExecutor = null;
        super.dispose();
    }

}
