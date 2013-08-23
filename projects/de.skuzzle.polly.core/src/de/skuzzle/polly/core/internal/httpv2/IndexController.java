package de.skuzzle.polly.core.internal.httpv2;

import java.util.Date;
import java.util.Map;

import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.Get;
import de.skuzzle.polly.http.api.HttpCookie;
import de.skuzzle.polly.http.api.HttpServer;
import de.skuzzle.polly.http.api.Param;
import de.skuzzle.polly.http.api.Post;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
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
    public HttpAnswer login(
        @Param("name") String name, 
        @Param("pw") String pw) {
        
        
        final UserManager um = this.getMyPolly().users();
        final User user = um.getUser(name);
        
        if (user != null && user.checkPassword(pw)) {
            this.getSession().set("user", user);
            final Map<String, Object> c = this.createContext("templates/home/home.html");
            c.put("user", user);
            this.getSession().set("loginTime", new Date());
            final Date now = new Date();
            this.getSession().setExpirationDate(
                new Date(now.getTime() + this.getServer().getSessionType()));
            
            // cookie to renew the session time out to count it from login time
            final HttpCookie renewTimeout = new HttpCookie(
                HttpServer.SESSION_ID_NAME, this.getSession().getId(), 
                this.getServer().sessionLiveTime() / 1000); 
            return this.makeAnswer(c).redirectTo("/").addCookie(renewTimeout);
        }
        
        return this.makeAnswer(this.createContext("templates/login.html"));
    }
    
    
    
    public final static class SessionTimeResult {
        public final String timeLeft;
        public final boolean logout;
        public SessionTimeResult(String timeLeft, boolean logout) {
            super();
            this.timeLeft = timeLeft;
            this.logout = logout;
        }
    }
    
    
    
    @Get("/api/sessionTime")
    public HttpAnswer getSessionTime() {
        System.out.println("SessionTime: " + getSession().getId());
        final User user = this.getSessionUser();
        if (user != null) {
            final long now = new Date().getTime();
            final long start = ((Date) this.getSession().getAttached("loginTime")).getTime();
            final long diff = now - start;
            long tl = this.getServer().sessionLiveTime() - diff;
            tl = (int) Math.ceil(tl / (double) 60000) * 60000; // round to minutes
            
            return new GsonHttpAnswer(200, new SessionTimeResult(
                this.getMyPolly().formatting().formatTimeSpanMs(tl), 
                this.getSessionUser() == null));
        } else {
            return new GsonHttpAnswer(200, new SessionTimeResult("", true));
        }
    }
    
    
    
    @Get("/api/getStatus")
    public HttpAnswer getStatus() {
        final Map<String, Object> c = this.createContext("");
        return HttpAnswers.createTemplateAnswer("templates/home/home.status.html", c);
    }
    
    
    
    @Get("/logoutx")
    public HttpAnswer logout() {
        this.getSession().detach("user");
        this.getSession().kill();
        final Map<String, Object> c = this.createContext("templates/home/home.html");
        return this.makeAnswer(c).redirectTo("/");
    }

    
    
    @Get("/")
    public HttpAnswer index() {
        final Map<String, Object> c = this.createContext("templates/home/home.html");
        return this.makeAnswer(c);
    }
}
