package polly.core.irc;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.ConfigurationProvider;
import de.skuzzle.polly.tools.events.EventProvider;

import polly.configuration.ConfigurationProviderImpl;
import polly.core.ShutdownManagerImpl;
import polly.core.commands.CommandManagerImpl;
import polly.core.users.UserManagerImpl;
import polly.eventhandler.AutoLogoffHandler;
import polly.eventhandler.AutoLogonHandler;
import polly.eventhandler.GhostHandler;
import polly.eventhandler.IrcLoggingHandler;
import polly.eventhandler.MessageHandler;
import polly.eventhandler.TraceNickChangeHandler;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Provide;
import polly.moduleloader.annotations.Require;



@Module(
    requires = {
        @Require(component = IrcManagerImpl.class),
        @Require(component = ConfigurationProviderImpl.class),
        @Require(component = ShutdownManagerImpl.class),
        @Require(component = EventProvider.class),
        @Require(component = UserManagerImpl.class),
        @Require(component = CommandManagerImpl.class),
        @Require(component = ExecutorService.class),
    },
    provides = @Provide(component = MessageHandler.class)
)
public class IrcEventHandlerProvider extends AbstractModule {

    public IrcEventHandlerProvider(ModuleLoader loader) {
        super("IRC_EVENT_HANDLER_PROVIDER", loader, true);
    }
    
    
    
    @Override
    public void setup() throws SetupException {
        IrcManagerImpl ircManager = this.requireNow(IrcManagerImpl.class, true);
        CommandManagerImpl commandManager = this.requireNow(
                CommandManagerImpl.class, true);
        UserManagerImpl userManager = this.requireNow(UserManagerImpl.class, true);
        ExecutorService executor = this.requireNow(ExecutorService.class, true);
        ShutdownManagerImpl shutdownManager = this.requireNow(
                ShutdownManagerImpl.class, true);
        
        ConfigurationProvider configProvider = 
            this.requireNow(ConfigurationProviderImpl.class, true);
        Configuration ircConfig = null;
        try {
            ircConfig = configProvider.open(IrcManagerProvider.IRC_CONFIG_FILE);
        } catch (IOException e) {
            throw new SetupException(e);
        }
        
        boolean autoLogin = ircConfig.readBoolean(Configuration.AUTO_LOGIN);
        int parseErrorDetails = ircConfig.readInt(Configuration.PARSE_ERROR_DETAILS);
        int autoLoginTime = ircConfig.readInt(Configuration.AUTO_LOGIN_TIME);
        boolean ircLogging = ircConfig.readBoolean(Configuration.IRC_LOGGING);
        
        // setup handler for incoming irc messages that are to be parsed as a command.
        // XXX: Ensure that message handler is the first message listener to be added
        //      because it updates a users idle time
        MessageHandler handler = new MessageHandler(commandManager,
            userManager, executor, parseErrorDetails);
        ircManager.addMessageListener(handler);
        provideComponent(handler);
        
        
        // setup irc logger
        if (ircLogging) {
            IrcLoggingHandler ircConsoleLogger = new IrcLoggingHandler();
            ircManager.addMessageListener(ircConsoleLogger);
            ircManager.addNickChangeListener(ircConsoleLogger);
            ircManager.addJoinPartListener(ircConsoleLogger);
        }

        // this listener checks if we ghosted our original nick and changes our nickname
        // to the default one
        ircManager.addMessageListener(new GhostHandler());

        ircManager.addNickChangeListener(new TraceNickChangeHandler(
            userManager));
        

        // Setup auto login / logout handler
        if (autoLogin) {
            AutoLogonHandler logonHandler = new AutoLogonHandler(
                ircManager, userManager, autoLoginTime);

            ircManager.addUserSpottedListener(logonHandler);
            ircManager.addNickChangeListener(logonHandler);
            ircManager.addConnectionListener(logonHandler);
            userManager.addUserListener(logonHandler);

            shutdownManager.addDisposable(logonHandler);
        }
        AutoLogoffHandler logoffHandler = new AutoLogoffHandler(userManager, 
            ircManager);
        ircManager.addUserSpottedListener(logoffHandler);
        ircManager.addConnectionListener(logoffHandler);
    }

}
