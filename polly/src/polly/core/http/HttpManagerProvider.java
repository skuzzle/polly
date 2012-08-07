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
import polly.moduleloader.annotations.Require;



@Module(
    requires = {
        @Require(component = MyPollyImpl.class),
    },
    provides = @Provide(component = HttpManagerImpl.class))
public class HttpManagerProvider extends AbstractModule {

    private final static Logger logger = Logger.getLogger(HttpManagerProvider.class
        .getName());
    
    private MyPolly myPolly;
    
    public HttpManagerProvider(ModuleLoader loader) {
        super("HTTP_SERVER_PROVIDER", loader, false);
    }
    
    
    
    @Override
    public void beforeSetup() {
        this.myPolly = this.requireNow(MyPollyImpl.class, true);
    }

    
    
    @Override
    public void setup() throws SetupException {
        File templateRoot = new File("webinterface");
        final HttpManagerImpl sws = new HttpManagerImpl(this.myPolly,
            templateRoot, 
            81, 1000 * 60 * 10);
        sws.addHttpAction(new RootHttpAction(this.myPolly));
        sws.addHttpAction(new LoginHttpAction(this.myPolly));
        sws.addHttpAction(new LogoutHttpAction(this.myPolly));
        sws.addHttpAction(new UserPageHttpAction(this.myPolly));
        sws.addHttpAction(new UserInfoPageHttpAction(this.myPolly));
        sws.addHttpAction(new IRCPageHttpAction(this.myPolly));
        sws.addHttpAction(new RoleHttpAction(this.myPolly));
        
        sws.addMenuUrl("Users");
        sws.addMenuUrl("IRC");
        sws.addMenuUrl("Logs");
        sws.addMenuUrl("Roles");
        
        try {
            sws.startServer();
            this.provideComponent(sws);
        } catch (IOException e) {
            logger.error("Error while starting webserver: ", e);
            throw new SetupException(e);
        }
    }

}
