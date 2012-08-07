package polly.core.http;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.MyPolly;


import polly.core.http.actions.IRCPageHttpAction;
import polly.core.http.actions.LoginHttpAction;
import polly.core.http.actions.LogoutHttpAction;
import polly.core.http.actions.RoleHttpAction;
import polly.core.http.actions.RootHttpAction;
import polly.core.http.actions.UserInfoPageHttpAction;
import polly.core.http.actions.UserPageHttpAction;
import polly.core.mypolly.MyPollyImpl;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Provide;



@Module(
    provides = @Provide(component = HttpManagerImpl.class)
)
public class HttpManagerProvider extends AbstractModule {

    private final static Logger logger = Logger.getLogger(HttpManagerProvider.class
        .getName());
    
    private HttpManagerImpl httpManager;
    
    public HttpManagerProvider(ModuleLoader loader) {
        super("HTTP_SERVER_PROVIDER", loader, false);
    }

    
    
    @Override
    public void setup() throws SetupException {
        File templateRoot = new File("webinterface");
        this.httpManager = new HttpManagerImpl(
            templateRoot, 
            81, 1000 * 60 * 10);
        this.provideComponent(this.httpManager);

    }
    
    
    @Override
    public void run() throws Exception {
        MyPolly myPolly = this.requireNow(MyPollyImpl.class, false);
        // HACK: this avoids cyclic dependency
        this.httpManager.setMyPolly(myPolly);
        
        this.httpManager.addHttpAction(new RootHttpAction(myPolly));
        this.httpManager.addHttpAction(new LoginHttpAction(myPolly));
        this.httpManager.addHttpAction(new LogoutHttpAction(myPolly));
        this.httpManager.addHttpAction(new UserPageHttpAction(myPolly));
        this.httpManager.addHttpAction(new UserInfoPageHttpAction(myPolly));
        this.httpManager.addHttpAction(new IRCPageHttpAction(myPolly));
        this.httpManager.addHttpAction(new RoleHttpAction(myPolly));
        
        this.httpManager.addMenuUrl("Users");
        this.httpManager.addMenuUrl("IRC");
        this.httpManager.addMenuUrl("Logs");
        this.httpManager.addMenuUrl("Roles");
        
        try {
            this.httpManager.startServer();
        } catch (IOException e) {
            logger.error("Error while starting webserver: ", e);
            throw new SetupException(e);
        }
    }

}
