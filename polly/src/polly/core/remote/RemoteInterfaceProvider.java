package polly.core.remote;

import java.io.File;
import java.io.IOException;

import de.skuzzle.polly.sdk.Configuration;

import polly.configuration.ConfigurationProviderImpl;
import polly.core.ShutdownManagerImpl;
import polly.core.remote.tcp.AdministrationServer;
import polly.core.roles.RoleManagerImpl;
import polly.core.users.UserManagerImpl;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;


@Module(requires = {
    @Require(component = ConfigurationProviderImpl.class),
    @Require(component = UserManagerImpl.class),
    @Require(component = ShutdownManagerImpl.class)}
)
public class RemoteInterfaceProvider extends AbstractModule {

    public static final String SERVER_CONFIG = "porat.cfg";
    
    private AdministrationManager adminManager;
    private ShutdownManagerImpl shutdownManager;
    private ProtocolHandler protocolHandler;
    private AdministrationServer server;
    private UserManagerImpl userManager;
    private RoleManagerImpl roleManager;
    
    
    public RemoteInterfaceProvider(ModuleLoader loader) {
        super("PORAT_PROVIDER", loader, false);
    }
    
    
    
    @Override
    public void beforeSetup() {
        this.userManager = this.requireNow(UserManagerImpl.class);
        this.roleManager = this.requireNow(RoleManagerImpl.class);
        this.shutdownManager = this.requireNow(ShutdownManagerImpl.class);
    }
    
    
    
    @Override
    public void setup() throws SetupException {
        ConfigurationProviderImpl configProvider = 
            this.requireNow(ConfigurationProviderImpl.class);
        Configuration serverCfg = null;
        try {
            serverCfg = configProvider.open(SERVER_CONFIG);
        } catch (IOException e) {
            throw new SetupException(e);
        }
        
        this.initKeyStore(serverCfg.readString(
            Configuration.KEYSTORE_FILE), 
            serverCfg.readString(Configuration.KEYSTORE_PASSWORD));
        
        this.adminManager = new AdministrationManager(this.userManager, this.roleManager);
        this.protocolHandler = new ProtocolHandler(this.adminManager);
        try {
            this.server = new AdministrationServer(null, 24500, 5);
            this.shutdownManager.addDisposable(this.server);
            this.shutdownManager.addDisposable(this.adminManager);
            this.server.addObjectReceivedListener(this.protocolHandler);
            this.server.addConnectionListener(this.protocolHandler);
            this.server.listen();
        } catch (IOException e) {
            throw new SetupException(e);
        }
    }
    
    

    private void initKeyStore(String keyStore, String password) throws SetupException {
        File keyFile = new File(keyStore);
        
        if (!keyFile.exists()) {
            throw new SetupException("file not found: " + keyFile);
        }
        
        System.setProperty("javax.net.ssl.keyStore", keyStore);
        System.setProperty("javax.net.ssl.keyStorePassword", password);
    }
    
    
    
    @Override
    public void dispose() {
        this.adminManager = null;
        this.protocolHandler = null;
        this.server = null;
        this.shutdownManager = null;
        this.userManager = null;
        super.dispose();
    }
}
