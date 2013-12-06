package de.skuzzle.polly.core.internal.irc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

import de.skuzzle.polly.core.configuration.ConfigurationProviderImpl;
import de.skuzzle.polly.core.eventhandler.AutoLoginProvider;
import de.skuzzle.polly.core.eventhandler.AutoLogoffHandler;
import de.skuzzle.polly.core.eventhandler.AutoLogonHandler;
import de.skuzzle.polly.core.eventhandler.EuIrcAutoLoginProvider;
import de.skuzzle.polly.core.eventhandler.GhostHandler;
import de.skuzzle.polly.core.eventhandler.IrcLoggingHandler;
import de.skuzzle.polly.core.eventhandler.MessageHandler;
import de.skuzzle.polly.core.eventhandler.TraceNickChangeHandler;
import de.skuzzle.polly.core.internal.ShutdownManagerImpl;
import de.skuzzle.polly.core.internal.commands.CommandManagerImpl;
import de.skuzzle.polly.core.internal.users.UserManagerImpl;
import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.SetupException;
import de.skuzzle.polly.core.moduleloader.annotations.Module;
import de.skuzzle.polly.core.moduleloader.annotations.Provide;
import de.skuzzle.polly.core.moduleloader.annotations.Require;
import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.ConfigurationProvider;
import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.exceptions.AlreadySignedOnException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;
import de.skuzzle.polly.tools.events.EventProvider;




@Module(
    requires = {
        @Require(component = IrcManagerImpl.class),
        @Require(component = ConfigurationProviderImpl.class),
        @Require(component = ShutdownManagerImpl.class),
        @Require(component = EventProvider.class),
        @Require(component = UserManagerImpl.class),
        @Require(component = CommandManagerImpl.class),
        @Require(component = ExecutorService.class),
        @Require(component = BotConnectionSettings.class)
    },
    provides = @Provide(component = MessageHandler.class)
)
public class IrcEventHandlerProvider extends AbstractProvider {

    private final Collection<AutoLoginProvider> loginProviders;
    
    
    public IrcEventHandlerProvider(ModuleLoader loader) {
        super("IRC_EVENT_HANDLER_PROVIDER", loader, true);
        this.loginProviders = new ArrayList<>();
        this.loginProviders.add(new EuIrcAutoLoginProvider());
    }
    
    
    
    private AutoLoginProvider findProvider(String server) {
        for (final AutoLoginProvider provider : this.loginProviders) {
            if (provider.supportsNetwork(server)) {
                return provider;
            }
        }
        return new AutoLoginProvider() {
            @Override
            public boolean supportsNetwork(String server) {
                return false;
            }
            @Override
            public void requestAuthentification(String forUser, IrcManager irc) {
            }
            @Override
            public boolean processMessageEvent(MessageEvent e, UserManagerImpl users)
                    throws AlreadySignedOnException, UnknownUserException {
                return false;
            }
        };
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
        boolean reportUnknownCommand = 
            ircConfig.readBoolean(Configuration.REPORT_UNKNOWN_COMMAND_ERROR);
        
        // setup handler for incoming irc messages that are to be parsed as a command.
        // XXX: Ensure that message handler is the first message listener to be added
        //      because it updates a users idle time
        MessageHandler handler = new MessageHandler(commandManager,
            userManager, executor, parseErrorDetails, reportUnknownCommand);
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
            final BotConnectionSettings settings = this.requireNow(
                BotConnectionSettings.class, true);
            final AutoLoginProvider provider = this.findProvider(settings.getHostName());
            AutoLogonHandler logonHandler = new AutoLogonHandler(
                ircManager, userManager, provider, autoLoginTime);

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
