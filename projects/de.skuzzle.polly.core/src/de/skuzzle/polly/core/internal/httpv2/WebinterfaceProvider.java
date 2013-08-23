package de.skuzzle.polly.core.internal.httpv2;

import java.io.File;
import java.io.IOException;

import de.skuzzle.polly.core.configuration.ConfigurationProviderImpl;
import de.skuzzle.polly.core.internal.ModuleStates;
import de.skuzzle.polly.core.internal.ShutdownManagerImpl;
import de.skuzzle.polly.core.internal.mypolly.MyPollyImpl;
import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.SetupException;
import de.skuzzle.polly.core.moduleloader.annotations.Module;
import de.skuzzle.polly.core.moduleloader.annotations.Provide;
import de.skuzzle.polly.core.moduleloader.annotations.Require;
import de.skuzzle.polly.http.api.DefaultServerFactory;
import de.skuzzle.polly.http.api.FileHttpEventHandler;
import de.skuzzle.polly.http.api.HttpServer;
import de.skuzzle.polly.http.api.HttpServletServer;
import de.skuzzle.polly.http.api.ServerFactory;
import de.skuzzle.polly.http.internal.HttpServerCreator;
import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.ConfigurationProvider;
import de.skuzzle.polly.sdk.Disposable;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.httpv2.MenuEntry;

@Module(
    requires = {
        @Require(component = ConfigurationProviderImpl.class),
        @Require(component = ShutdownManagerImpl.class),
        @Require(state = ModuleStates.PERSISTENCE_READY)
    },
    provides = @Provide(component = HttpManagerV2Impl.class)
)
public class WebinterfaceProvider extends AbstractProvider {
    
    public final static String HTTP_CONFIG = "http.cfg";
    private Configuration serverCfg;
    private HttpServletServer server;
    private HttpManagerV2Impl httpManager;
    
    
    public WebinterfaceProvider(ModuleLoader loader) {
        super("WEBINTERFACE_PROVIDER", loader, false);
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
        
        int port = 81; //this.serverCfg.readInt(Configuration.HTTP_PORT);
        int sessionTimeout = this.serverCfg.readInt(Configuration.HTTP_SESSION_TIMEOUT);
        
        final ServerFactory sf = new DefaultServerFactory(port);
        this.server = HttpServerCreator.createServletServer(sf);
        this.server.setSessionLiveTime(sessionTimeout);
        this.server.setSessionType(HttpServer.SESSION_TYPE_COOKIE);
        this.server.addWebRoot(new File("webv2"));
        this.server.addAnswerHandler(GsonHttpAnswer.class, new GsonHttpAnswerHandler());
        
        ShutdownManagerImpl sm = this.requireNow(ShutdownManagerImpl.class, true);
        sm.addDisposable(new Disposable() {
            
            @Override
            public boolean isDisposed() {
                return false;
            }
            
            
            
            @Override
            public void dispose() throws DisposingException {
                server.shutdown(1000);
            }
        });
        
        this.httpManager = new HttpManagerV2Impl(this.server);
        this.provideComponent(this.httpManager);
        
    }
    
    
    
    @Override
    public void run() throws Exception {
        final MyPolly myPolly = this.requireNow(MyPollyImpl.class, false);
        
        this.server.addController(new IndexController(myPolly, this.httpManager));
        this.server.addController(new SessionController(myPolly, this.httpManager));
        this.server.addController(new UserController(myPolly, this.httpManager));
        
        this.httpManager.addMenuEntry(new MenuEntry("Status", "content/status"));
        this.httpManager.addMenuEntry(new MenuEntry("Sessions", "content/sessions"));
        this.httpManager.addMenuEntry(new MenuEntry("Users", "content/users"));
        
        this.server.addHttpEventHandler("/files", new FileHttpEventHandler(false));
        
        this.server.start();
    }
}
