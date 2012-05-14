package polly.core.http;

import java.io.IOException;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.MyPolly;


import polly.core.http.actions.IRCPageHttpAction;
import polly.core.http.actions.LoginHttpAction;
import polly.core.http.actions.LogoutHttpAction;
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
    requires = @Require(component = MyPollyImpl.class), 
    provides = @Provide(component = SimpleWebServer.class))
public class HttpModule extends AbstractModule {

    private final static Logger logger = Logger.getLogger(HttpModule.class
        .getName());
    
    private MyPolly myPolly;
    
    public HttpModule(ModuleLoader loader) {
        super("HTTP_MODULE", loader, false);
    }
    
    
    
    @Override
    public void beforeSetup() {
        this.myPolly = this.requireNow(MyPollyImpl.class);
    }

    
    
    @Override
    public void setup() throws SetupException {
        final SimpleWebServer sws = new SimpleWebServer(8000);
        sws.addHttpAction(new RootHttpAction(this.myPolly));
        sws.addHttpAction(new LoginHttpAction(this.myPolly.users()));
        sws.addHttpAction(new LogoutHttpAction());
        sws.addHttpAction(new UserPageHttpAction(this.myPolly));
        sws.addHttpAction(new UserInfoPageHttpAction(this.myPolly));
        sws.addHttpAction(new IRCPageHttpAction(this.myPolly));
        
        sws.addMenuUrl("Users");
        sws.addMenuUrl("IRC");
        sws.addMenuUrl("Logs");
        try {
            sws.startServer();
            this.provideComponent(sws);
        } catch (IOException e) {
            logger.error("Error while starting webserver: ", e);
        }
    }

}
