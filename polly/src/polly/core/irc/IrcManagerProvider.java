package polly.core.irc;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.jibble.pircbot.NickAlreadyInUseException;

import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.ConfigurationProvider;

import polly.configuration.ConfigurationProviderImpl;
import polly.core.DefaultUserAttributesProvider;
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
        @Require(component = ConfigurationProviderImpl.class),
        @Require(component = ShutdownManagerImpl.class),
        @Require(component = EventProvider.class),
        @Require(component = UserManagerImpl.class),
        @Require(component = CommandManagerImpl.class),
        @Require(component = ExecutorService.class),
        @Require(component = DefaultUserAttributesProvider.class),
        @Require(state = ModuleStates.PLUGINS_READY),
        @Require(state = ModuleStates.PERSISTENCE_READY) }, 
    provides = {
        @Provide(component = IrcManagerImpl.class),
        @Provide(component = MessageHandler.class),
        @Provide(state = ModuleStates.IRC_READY) })
public class IrcManagerProvider extends AbstractModule {

    
    public final static String IRC_CONFIG_FILE = "irc.cfg";
    
    private EventProvider events;
    private ExecutorService commandExecutor;
    private CommandManagerImpl commandManager;
    private UserManagerImpl userManager;
    private IrcManagerImpl ircManager;
    private ShutdownManagerImpl shutdownManager;

    private BotConnectionSettings connectionSettings;



    public IrcManagerProvider(ModuleLoader loader) {
        super("IRC_MANAGER_PROVIDER", loader, true);
    }



    @Override
    public void beforeSetup() {
        this.events = this.requireNow(EventProvider.class);
        this.userManager = this.requireNow(UserManagerImpl.class);
        this.commandManager = this.requireNow(CommandManagerImpl.class);
        this.shutdownManager = this.requireNow(ShutdownManagerImpl.class);
        this.commandExecutor = this.requireNow(ExecutorService.class);
    }



    @Override
    public void setup() throws SetupException {
        ConfigurationProvider configProvider = 
            this.requireNow(ConfigurationProviderImpl.class);
        Configuration ircConfig = null;
        try {
            ircConfig = configProvider.open(IRC_CONFIG_FILE);
        } catch (IOException e) {
            throw new SetupException(e);
        }
        
        String nickName = ircConfig.readString(Configuration.NICKNAME);
        String server = ircConfig.readString(Configuration.SERVER);
        String ident = ircConfig.readString(Configuration.IDENT);
        String ircModes = ircConfig.readString(Configuration.IRC_MODES);
        List<String> channels = ircConfig.readStringList(Configuration.CHANNELS);
        int port = ircConfig.readInt(Configuration.PORT);
        boolean ircLogging = ircConfig.readBoolean(Configuration.IRC_LOGGING);
        boolean autoLogin = ircConfig.readBoolean(Configuration.AUTO_LOGIN);
        String encodingName = configProvider.getRootConfiguration().readString(
            Configuration.ENCODING);
        int parseErrorDetails = ircConfig.readInt(Configuration.PARSE_ERROR_DETAILS);
        int autoLoginTime = ircConfig.readInt(Configuration.AUTO_LOGIN_TIME);
        
        this.ircManager = new IrcManagerImpl(nickName,
            this.events, ircConfig, encodingName);

        this.provideComponent(this.ircManager);

        logger.info("Starting bot with settings: (" + "Nick: "
            + nickName + ", Ident: *****" + ", Server: "
            + server + ", Port: " + port
            + ", Logging: " + ircLogging + ")");

        // setup handler for incoming irc messages that are to be parsed as a command.
        // XXX: Ensure that message handler is the first message listener to be added
        //      because it updates a users idle time
        MessageHandler handler = new MessageHandler(this.commandManager,
            this.userManager, this.commandExecutor, parseErrorDetails);
        this.ircManager.addMessageListener(handler);
        this.provideComponent(handler);
        
        
        // setup irc logger
        if (ircLogging) {
            IrcLoggingHandler ircConsoleLogger = new IrcLoggingHandler();
            this.ircManager.addMessageListener(ircConsoleLogger);
            this.ircManager.addNickChangeListener(ircConsoleLogger);
            this.ircManager.addJoinPartListener(ircConsoleLogger);
        }

        // this listener checks if we ghosted our original nick and changes our nickname
        // to the default one
        this.ircManager.addMessageListener(new GhostHandler());

        this.ircManager.addNickChangeListener(new TraceNickChangeHandler(
            this.userManager));
        

        // Setup auto login / logout handler
        if (autoLogin) {
            AutoLogonHandler logonHandler = new AutoLogonHandler(
                this.ircManager, this.userManager, 
                autoLoginTime);

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
            nickName, server,
            port, ident,
            channels, ircModes);

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
        this.events = null;
        this.userManager = null;
        this.commandManager = null;
        this.shutdownManager = null;
        this.commandExecutor = null;
        super.dispose();
    }

}
