package polly.core.http;

import java.io.IOException;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.http.AbstractHttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;


import polly.core.http.actions.LoginHttpAction;
import polly.core.http.actions.LogoutHttpAction;
import polly.core.http.actions.RootHttpAction;
import polly.core.users.UserManagerImpl;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Provide;
import polly.moduleloader.annotations.Require;

@Module(
    requires = @Require(component=UserManagerImpl.class), 
    provides = @Provide(component = SimpleWebServer.class))
public class HttpModule extends AbstractModule {

    private final static Logger logger = Logger.getLogger(HttpModule.class
        .getName());
    
    private UserManagerImpl userManager;
    
    public HttpModule(ModuleLoader loader) {
        super("HTTP_MODULE", loader, false);
    }
    
    
    
    @Override
    public void beforeSetup() {
        this.userManager = this.requireNow(UserManagerImpl.class);
    }

    
    
    @Override
    public void setup() throws SetupException {
        final SimpleWebServer sws = new SimpleWebServer(8000);
        sws.addHttpAction(new RootHttpAction());
        sws.addHttpAction(new LoginHttpAction(this.userManager));
        sws.addHttpAction(new LogoutHttpAction());
        sws.addHttpAction(new AbstractHttpAction("/test") {
            
            @Override
            public void execute(HttpEvent e, HttpTemplateContext c) {
                c.put("heading", "nurn test");
            }
        });
        
        sws.addMenuUrl("Login");
        sws.addMenuUrl("Test");
        sws.addMenuUrl("Test2");
        try {
            sws.startServer();
            this.provideComponent(sws);
        } catch (IOException e) {
            logger.error("Error while starting webserver: ", e);
        }
    }

}
