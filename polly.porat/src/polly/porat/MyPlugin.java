package polly.porat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import polly.porat.core.AdministrationManager;
import polly.porat.core.ProtocolHandler;
import polly.porat.core.tcp.AdministrationServer;

import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;
import de.skuzzle.polly.sdk.exceptions.PluginException;

public class MyPlugin extends PollyPlugin {

    public static final String SERVER_CONFIG = "porat.cfg";
    
    private AdministrationManager adminManager;
    private ProtocolHandler protocolHandler;
    private AdministrationServer server;
    
    
    
    public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException, 
            FileNotFoundException, IOException, PluginException {
        super(myPolly);
        
        Configuration serverCfg = myPolly.configuration().open(SERVER_CONFIG);
        this.initKeyStore(
            serverCfg.readString(Configuration.KEYSTORE_FILE), 
            serverCfg.readString(Configuration.KEYSTORE_PASSWORD));
        
        this.adminManager = new AdministrationManager(myPolly.users(), myPolly.roles());
        this.protocolHandler = new ProtocolHandler(this.adminManager);
        
        this.server = new AdministrationServer(null, 24500, 5);
        myPolly.shutdownManager().addDisposable(this.server);
        myPolly.shutdownManager().addDisposable(this.adminManager);
        this.server.addObjectReceivedListener(this.protocolHandler);
        this.server.addConnectionListener(this.protocolHandler);
        this.server.listen();
    }

    
    
    private void initKeyStore(String keyStore, String password) throws PluginException {
        File keyFile = new File(keyStore);
        
        if (!keyFile.exists()) {
            throw new PluginException("file not found: " + keyFile);
        }
        
        System.setProperty("javax.net.ssl.keyStore", keyStore);
        System.setProperty("javax.net.ssl.keyStorePassword", password);
    }
}
