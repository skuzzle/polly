package polly.core.irc;


import java.io.IOException;
import java.util.List;

import org.jibble.pircbot.NickAlreadyInUseException;

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
        String encodingName = configProvider.getRootConfiguration().readString(
            Configuration.ENCODING);

        
        this.ircManager = new IrcManagerImpl(nickName,
            this.events, ircConfig, encodingName);

        this.provideComponent(this.ircManager);

        // XXX: do not add any listeners to the irc manager here. this is done
        //      in IrcEventHandlerProvider
        logger.info("Starting bot with settings: (" + "Nick: "
            + nickName + ", Ident: *****" + ", Server: "
            + server + ", Port: " + port
            + ", Logging: " + ircLogging + ")");

            
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
        this.shutdownManager = null;
        super.dispose();
    }

}
