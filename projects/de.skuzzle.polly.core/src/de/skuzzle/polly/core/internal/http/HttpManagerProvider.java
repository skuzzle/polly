package de.skuzzle.polly.core.internal.http;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import de.skuzzle.polly.core.configuration.ConfigurationProviderImpl;
import de.skuzzle.polly.core.internal.ModuleStates;
import de.skuzzle.polly.core.internal.ShutdownManagerImpl;
import de.skuzzle.polly.core.internal.http.actions.IRCPageHttpAction;
import de.skuzzle.polly.core.internal.http.actions.LoginHttpAction;
import de.skuzzle.polly.core.internal.http.actions.LogoutHttpAction;
import de.skuzzle.polly.core.internal.http.actions.RoleHttpAction;
import de.skuzzle.polly.core.internal.http.actions.RootHttpAction;
import de.skuzzle.polly.core.internal.http.actions.SessionPageHttpAction;
import de.skuzzle.polly.core.internal.http.actions.ShutdownHttpAction;
import de.skuzzle.polly.core.internal.http.actions.UserInfoPageHttpAction;
import de.skuzzle.polly.core.internal.http.actions.UserPageHttpAction;
import de.skuzzle.polly.core.internal.mypolly.MyPollyImpl;
import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.SetupException;
import de.skuzzle.polly.core.moduleloader.annotations.Module;
import de.skuzzle.polly.core.moduleloader.annotations.Provide;
import de.skuzzle.polly.core.moduleloader.annotations.Require;
import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.ConfigurationProvider;
import de.skuzzle.polly.sdk.MyPolly;





@Module(
    requires = {
        @Require(component = ConfigurationProviderImpl.class),
        @Require(component = ShutdownManagerImpl.class),
        @Require(state = ModuleStates.PERSISTENCE_READY)
    },
    provides = @Provide(component = HttpManagerImpl.class)
)
public class HttpManagerProvider extends AbstractProvider {

    private final static Logger logger = Logger.getLogger(HttpManagerProvider.class
        .getName());
    
    public final static String HTTP_CONFIG = "http.cfg";
    public final static String HOME_PAGE = "HOME_PAGE";
    
    private HttpManagerImpl httpManager;
    private Configuration serverCfg;
    
    
    public HttpManagerProvider(ModuleLoader loader) {
        super("HTTP_SERVER_PROVIDER", loader, true);
    }

    
    
    @Override
    public void setup() throws SetupException {
        ConfigurationProvider configProvider = this.requireNow(
                ConfigurationProviderImpl.class, true);
        
        try {
            this.serverCfg = configProvider.open(HTTP_CONFIG);
        } catch (IOException e) {
            throw new SetupException(e);
        }
        
        
        File templateRoot = new File(
                this.serverCfg.readString(Configuration.HTTP_TEMPLATE_ROOT));
        int sessionTimeOut = this.serverCfg.readInt(Configuration.HTTP_SESSION_TIMEOUT);
        int port = this.serverCfg.readInt(Configuration.HTTP_PORT);
        String publicHost = this.serverCfg.readString(Configuration.HTTP_PUBLIC_HOST);
        String encoding = configProvider.getRootConfiguration().readString(
                Configuration.ENCODING);
        int cacheThreshold = this.serverCfg.readInt(
                Configuration.HTTP_SESSION_CACHE_THRESHOLD);
        int errorThreshold = this.serverCfg.readInt(Configuration.HTTP_ERROR_THRESHOLD);
        
        this.httpManager = new HttpManagerImpl(
            templateRoot, publicHost,
            port, sessionTimeOut, encoding, cacheThreshold, errorThreshold);
        
        this.provideComponent(this.httpManager);
        ShutdownManagerImpl shutdownManager = this.requireNow(
                ShutdownManagerImpl.class, true);
        shutdownManager.addDisposable(this.httpManager);
    }
    
    
    
    @Override
    public void run() throws Exception {
        final MyPolly myPolly = this.requireNow(MyPollyImpl.class, false);
        // HACK: this avoids cyclic dependency
        this.httpManager.setMyPolly(myPolly);
        
        
        logger.trace("Adding default http actions...");
        this.httpManager.addHttpAction(new RootHttpAction(myPolly));
        this.httpManager.addHttpAction(new LoginHttpAction(myPolly));
        this.httpManager.addHttpAction(new LogoutHttpAction(myPolly));
        this.httpManager.addHttpAction(new UserPageHttpAction(myPolly));
        this.httpManager.addHttpAction(new UserInfoPageHttpAction(myPolly));
        this.httpManager.addHttpAction(new IRCPageHttpAction(myPolly));
        this.httpManager.addHttpAction(new RoleHttpAction(myPolly));
        this.httpManager.addHttpAction(new SessionPageHttpAction(myPolly));
        this.httpManager.addHttpAction(new ShutdownHttpAction(myPolly));
        
        logger.trace("Adding menu entries for default admin menu...");
        this.httpManager.addMenuUrl("Admin", "Users");
        //this.httpManager.addMenuUrl("Admin", "IRC");
        this.httpManager.addMenuUrl("Admin", "Logs");
        this.httpManager.addMenuUrl("Admin", "Roles");
        this.httpManager.addMenuUrl("Admin", "Sessions");

        
        boolean start = this.serverCfg.readBoolean(Configuration.HTTP_START_SERVER);
        if (!start) {
            logger.info("Webserver will not be started due to configuration settings");
            return;
        }
        
        try {
            logger.trace("Trying to start the http service...");
            this.httpManager.startWebServer();
            logger.info("Webserver started");
        } catch (IOException e) {
            logger.error("Error while starting webserver: ", e);
            throw new SetupException(e);
        }
    }

}
