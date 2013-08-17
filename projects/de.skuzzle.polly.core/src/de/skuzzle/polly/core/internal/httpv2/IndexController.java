package de.skuzzle.polly.core.internal.httpv2;

import java.util.Map;

import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.Get;
import de.skuzzle.polly.http.api.HttpCookie;
import de.skuzzle.polly.http.api.HttpServer;
import de.skuzzle.polly.http.api.Param;
import de.skuzzle.polly.http.api.Post;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.httpv2.HttpManagerV2;


public class IndexController extends PollyController {

    
    public IndexController(MyPolly myPolly, HttpManagerV2 httpManager) {
        super(myPolly, httpManager);
    }
    
    
    
    @Override
    protected Controller createInstance() {
        return new IndexController(this.getMyPolly(), this.getHttpManager());
    }

    
    
    @Post("/login")
    public HttpAnswer login(@Param("name") String name, @Param("pw") String pw) {
        final UserManager um = this.getMyPolly().users();
        final User user = um.getUser(name);
        
        if (user != null && user.checkPassword(pw)) {
            this.getSession().attach("user", user);
            final Map<String, Object> c = this.createContext("templates/home.html");
            c.put("user", user);
            
            // cookie to renew the session time out to count it from login time
            final HttpCookie renewTimeout = new HttpCookie(
                HttpServer.SESSION_ID_NAME, this.getSession().getId(), 
                this.getServer().sessionLiveTime() / 1000); 
            return this.makeAnswer(c).redirect("/").addCookie(renewTimeout);
        }
        
        final Map<String, Object> c = this.createContext("templates/login.html");
        return this.makeAnswer(c);
    }
    
    
    
    @Get("/logoutx")
    public HttpAnswer logout() {
        this.getSession().detach("user");
        final Map<String, Object> c = this.createContext("templates/home.html");
        return this.makeAnswer(c).redirect("/").addCookie(
            new HttpCookie(HttpServer.SESSION_ID_NAME, this.getSession().getId(), 10));
    }

    
    
    @Get("/")
    public HttpAnswer index() {
        final Map<String, Object> c = this.createContext("templates/home.html");
        return this.makeAnswer(c);
    }
}
