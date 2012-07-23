package polly.core.irc;


import org.jibble.pircbot.NickAlreadyInUseException;

import polly.configuration.PollyConfiguration;
import polly.core.DefaultUserAttributesProvider;
import polly.core.ShutdownManagerImpl;
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
        @Require(component = DefaultUserAttributesProvider.class),
        @Require(component = EventProvider.class),
        @Require(state = ModuleStates.PLUGINS_READY),
        @Require(state = ModuleStates.PERSISTENCE_READY) }, 
    provides = {
        @Provide(component = IrcManagerImpl.class),
        @Provide(state = ModuleStates.IRC_READY) })
public class IrcManagerProvider extends AbstractModule {

    private EventProvider events;
    private IrcManagerImpl ircManager;
    private PollyConfiguration config;
    private ShutdownManagerImpl shutdownManager;

    private BotConnectionSettings connectionSettings;



    public IrcManagerProvider(ModuleLoader loader) {
        super("IRC_MANAGER_PROVIDER", loader, true);
    }



    @Override
    public void beforeSetup() {
        this.config = this.requireNow(PollyConfiguration.class);
        this.events = this.requireNow(EventProvider.class);
        this.shutdownManager = this.requireNow(ShutdownManagerImpl.class);
    }



    @Override
    public void setup() throws SetupException {
        this.ircManager = new IrcManagerImpl(this.config.getNickName(),
            this.events, this.config);

        this.provideComponent(this.ircManager);

        // XXX: do not add any listeners to the irc manager here. this is done
        //      in IrcEventHandlerProvider
        logger.info("Starting bot with settings: (" + "Nick: "
            + this.config.getNickName() + ", Ident: *****" + ", Server: "
            + this.config.getServer() + ", Port: " + this.config.getPort()
            + ", Logging: " + this.config.getIrcLogging() + ")");

            
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
        this.shutdownManager = null;
        super.dispose();
    }

}
