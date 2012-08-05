package polly.core.irc;


import java.io.IOException;
import java.util.List;


import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.ConfigurationProvider;

import polly.configuration.ConfigurationProviderImpl;
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
        @Require(component = ConfigurationProviderImpl.class),
        @Require(component = ShutdownManagerImpl.class),
        @Require(component = DefaultUserAttributesProvider.class),
        @Require(component = EventProvider.class),
        @Require(state = ModuleStates.PLUGINS_READY),
        @Require(state = ModuleStates.PERSISTENCE_READY) }, 
    provides = {
        @Provide(component = IrcManagerImpl.class),
        @Provide(state = ModuleStates.IRC_READY) })
public class IrcManagerProvider extends AbstractModule {

    
    public final static String IRC_CONFIG_FILE = "irc.cfg";
    
    private EventProvider events;
    private IrcManagerImpl ircManager;
    private ShutdownManagerImpl shutdownManager;
    private Configuration ircConfig;
    
    private BotConnectionSettings connectionSettings;



    public IrcManagerProvider(ModuleLoader loader) {
        super("IRC_MANAGER_PROVIDER", loader, true);
    }



    @Override
    public void beforeSetup() {
        this.events = this.requireNow(EventProvider.class);
        this.shutdownManager = this.requireNow(ShutdownManagerImpl.class);
    }



    @Override
    public void setup() throws SetupException {
        ConfigurationProvider configProvider = 
            this.requireNow(ConfigurationProviderImpl.class);
        this.ircConfig = null;
        try {
            this.ircConfig = configProvider.open(IRC_CONFIG_FILE);
        } catch (IOException e) {
            throw new SetupException(e);
        }
        
        String nickName = this.ircConfig.readString(Configuration.NICKNAME);
        String server = this.ircConfig.readString(Configuration.SERVER);
        String ident = this.ircConfig.readString(Configuration.IDENT);
        String ircModes = this.ircConfig.readString(Configuration.IRC_MODES);
        List<String> channels = this.ircConfig.readStringList(Configuration.CHANNELS);
        List<Integer> ports = this.ircConfig.readIntList(Configuration.PORT);
        boolean ircLogging = this.ircConfig.readBoolean(Configuration.IRC_LOGGING);
        String encodingName = configProvider.getRootConfiguration().readString(
            Configuration.ENCODING);

        
        this.ircManager = new IrcManagerImpl(nickName,
            this.events, this.ircConfig, encodingName);

        this.provideComponent(this.ircManager);

        // XXX: do not add any listeners to the irc manager here. this is done
        //      in IrcEventHandlerProvider
        logger.info("Starting bot with settings: (" + "Nick: "
            + nickName + ", Ident: *****" + ", Server: "
            + server + ", Ports: " + ports.toString()
            + ", Logging: " + ircLogging + ")");

            
        this.connectionSettings = new BotConnectionSettings(
            nickName, server,
            ports, ident,
            channels, ircModes);

        this.shutdownManager.addDisposable(this.ircManager);
    }



    @Override
    public void run() throws Exception {
        int retries = this.ircConfig.readInt(Configuration.INITIAL_RETY_COUNT);
        for (int i = 0; i < retries && !this.ircManager.isConnected(); ++i) {
            logger.warn("Initial connection attempt " + (i + 1));
            try {
                this.ircManager.connect(this.connectionSettings);
            } catch (Exception e) {
                logger.error("Connection attempt failed", e);
            }
        }
        if (!this.ircManager.isConnected()) {
            throw new SetupException("Failed to connect after " + retries + 
                    " connection attempts");
        }
        logger.info("IRC Connection established!");
        this.addState(ModuleStates.IRC_READY);
    }
    
    
    
    @Override
    public void dispose() {
        this.events = null;
        this.shutdownManager = null;
        super.dispose();
    }

}
