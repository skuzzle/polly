package de.skuzzle.polly.core.internal.httpv2;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import de.skuzzle.polly.core.Polly;
import de.skuzzle.polly.core.configuration.ConfigurationProviderImpl;
import de.skuzzle.polly.core.internal.ModuleStates;
import de.skuzzle.polly.core.internal.ShutdownManagerImpl;
import de.skuzzle.polly.core.internal.mypolly.MyPollyImpl;
import de.skuzzle.polly.core.internal.plugins.PluginManagerImpl;
import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.SetupException;
import de.skuzzle.polly.core.moduleloader.annotations.Module;
import de.skuzzle.polly.core.moduleloader.annotations.Provide;
import de.skuzzle.polly.core.moduleloader.annotations.Require;
import de.skuzzle.polly.http.api.AddHandlerListener;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.DefaultServerFactory;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.HttpEventListener;
import de.skuzzle.polly.http.api.HttpException;
import de.skuzzle.polly.http.api.HttpServer;
import de.skuzzle.polly.http.api.HttpServletServer;
import de.skuzzle.polly.http.api.ServerFactory;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswerHandler;
import de.skuzzle.polly.http.api.answers.HttpResourceAnswer;
import de.skuzzle.polly.http.api.answers.HttpTemplateAnswer;
import de.skuzzle.polly.http.api.handler.DirectoryEventHandler;
import de.skuzzle.polly.http.api.handler.HttpEventHandler;
import de.skuzzle.polly.http.internal.HttpServerCreator;
import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.ConfigurationProvider;
import de.skuzzle.polly.sdk.Disposable;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.httpv2.GsonHttpAnswer;
import de.skuzzle.polly.sdk.httpv2.MenuCategory;
import de.skuzzle.polly.sdk.httpv2.MenuEntry;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.resources.PollyBundle;
import de.skuzzle.polly.sdk.resources.Resources;
import de.skuzzle.polly.tools.concurrent.ThreadFactoryBuilder;

@Module(
    requires = {
        @Require(component = ConfigurationProviderImpl.class),
        @Require(component = ShutdownManagerImpl.class),
        @Require(component = PluginManagerImpl.class),
        @Require(state = ModuleStates.PERSISTENCE_READY),
        @Require(state = ModuleStates.PLUGINS_READY)
    },
    provides = @Provide(component = WebInterfaceManagerImpl.class)
)
public class WebinterfaceProvider extends AbstractProvider {
    
    private final static Logger logger = Logger.getLogger(WebinterfaceProvider.class
        .getName());
    
    public final static String HTTP_CONFIG = "http.cfg"; //$NON-NLS-1$
    private Configuration serverCfg;
    private HttpServletServer server;
    private WebInterfaceManagerImpl webinterface;
    
    
    public WebinterfaceProvider(ModuleLoader loader) {
        super("WEBINTERFACE_PROVIDER", loader, false); //$NON-NLS-1$
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
        
        int port = this.serverCfg.readInt(Configuration.HTTP_PORT);
        int sessionTimeout = this.serverCfg.readInt(Configuration.HTTP_SESSION_TIMEOUT);
        
        final ExecutorService executor = Executors.newFixedThreadPool(15, 
                new ThreadFactoryBuilder("HTTP_%n%")); //$NON-NLS-1$
        
        final ServerFactory sf;
        final boolean useSSL = this.serverCfg.readBoolean(Configuration.HTTP_USE_SSL);
        
        if (useSSL) {
            final String keyStorePath = this.serverCfg.readString(Configuration.KEYSTORE_FILE);
            final String keyStorePW = this.serverCfg.readString(Configuration.KEYSTORE_PASSWORD);
            final String keyPW = this.serverCfg.readString(Configuration.KEY_PASSWORD);
            sf = new SSLServerFactory(port, executor, keyStorePath, keyStorePW, keyPW);
        } else {
            sf = new DefaultServerFactory(port, executor);
        }
        this.server = HttpServerCreator.createServletServer(sf);
        this.server.setSessionLiveTime(sessionTimeout);
        this.server.setSessionType(HttpServer.SESSION_TYPE_COOKIE);
        this.server.setAnswerHandler(GsonHttpAnswer.class, new GsonHttpAnswerHandler());
        this.server.addHttpEventListener(new HttpEventListener() {
            
            @Override
            public void onRequest(HttpEvent e) {
                logger.debug("HTTP: " + e.getMode() + " " +  //$NON-NLS-1$ //$NON-NLS-2$
                    e.getRequestURI().toString() + 
                    " [ip: " + e.getClientIP() + ", " +  //$NON-NLS-1$ //$NON-NLS-2$
                    e.getSession().getAttached("user") + "], " +  //$NON-NLS-1$ //$NON-NLS-2$
                    e.parameterMap());
            }
        });
        
        ShutdownManagerImpl sm = this.requireNow(ShutdownManagerImpl.class, true);
        sm.addDisposable(new Disposable() {
            
            @Override
            public boolean isDisposed() {
                return false;
            }
            
            
            
            @Override
            public void dispose() throws DisposingException {
                server.shutdown(5);
            }
        });
        
        this.webinterface = new WebInterfaceManagerImpl(this.server, "webv2", 
                this.serverCfg.readString(Configuration.HTTP_PUBLIC_HOST), port, useSSL);

        // Automatically collect all menu entries
        this.server.addAddHandlerListener(new AddHandlerListener() {
            @Override
            public void handlerAdded(Controller c, String url, String name, String[] values) {
                if (values.length < 1 || !values[0].equals(WebinterfaceManager.ADD_MENU_ENTRY)) {
                    return;
                } else if (values.length < 4) {
                    logger.error("Ignoring 'ADD_MENU_EVENT' beacause of missing parameters"); //$NON-NLS-1$
                    return;
                }
                
                final String bundleFamily = values[1];
                final String categoryKey = values[2];
                final String descriptionKey = values[3];
                final String[] permissions;
                if (values.length == 4) {
                    permissions = new String[0];
                } else {
                    permissions = Arrays.copyOfRange(values, 4, values.length);
                }
                
                final PollyBundle pb = Resources.get(bundleFamily, 
                        c.getClass().getClassLoader());
                final String description = pb.get(descriptionKey);
                final String category = pb.get(categoryKey);
                name = pb.get(name);
                webinterface.addMenuEntry(category, 
                    new MenuEntry(name, url, description, permissions));
            }
        });
        
        this.provideComponent(this.webinterface);
    }
    
    
    
    @Override
    public void run() throws Exception {
        final MyPolly myPolly = this.requireNow(MyPollyImpl.class, false);
        
        this.webinterface.addCategory(new MenuCategory(Integer.MAX_VALUE, MSG.indexAdminCategory));
        this.webinterface.addCategory(new MenuCategory(-100, MSG.indexGeneralCategory));
        this.server.addController(new IndexController(myPolly));
        this.server.addController(new SessionController(myPolly));
        this.server.addController(new UserController(myPolly));
        this.server.addController(new RoleController(myPolly));
        UserController.createUserTable(myPolly);
        
        
        // replace the default template answer handler
        final PluginManagerImpl pluginManager = this.requireNow(
                PluginManagerImpl.class, true);
        
        final HttpAnswerHandler replace = new PollyTemplateAnswerHandler(
                Polly.PLUGIN_FOLDER, pluginManager.loadedPlugins());
        this.server.setAnswerHandler(HttpTemplateAnswer.class, replace);
        
        
        this.server.addHttpEventHandler("/files", 
            new DirectoryEventHandler("webv2/files", false));
        
        final ClassLoader cl = this.getClass().getClassLoader();
        this.server.addHttpEventHandler("/de/skuzzle/polly/sdk/httpv2", new HttpEventHandler() {
            @Override
            public HttpAnswer handleHttpEvent(String registered, HttpEvent e,
                    HttpEventHandler next) throws HttpException {
                return new HttpResourceAnswer(200, cl, e.getPlainUri());
            }
        });  
        this.server.start();
    }
}
