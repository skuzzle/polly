package de.skuzzle.polly.core.internal.httpv2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import de.skuzzle.polly.sdk.httpv2.MenuEntry;


public class IndexController extends PollyController {

    
    public IndexController(MyPolly myPolly, HttpManagerV2 httpManager) {
        super(myPolly, httpManager);
    }
    
    
    
    @Override
    protected Controller createInstance() {
        return new IndexController(this.getMyPolly(), this.getHttpManager());
    }

    
    
    public final static class LoginResult {
        public boolean success;
        public String contentPage;
        public String userName;
        public final List<MenuEntry> menu;
        
        public LoginResult(boolean success, String contentPage, String userName) {
            super();
            this.success = success;
            this.contentPage = contentPage;
            this.userName = userName;
            this.menu = new ArrayList<>();
        }
    }
    
    
    
    @Post("/api/login")
    public HttpAnswer login(
        @Param("name") String name, 
        @Param("pw") String pw) {
        
        System.out.println("Login attempt: " + name);
        
        final UserManager um = this.getMyPolly().users();
        final User user = um.getUser(name);
        
        if (user != null && user.checkPassword(pw)) {
            this.getSession().set("user", user);
            final Map<String, Object> c = this.createContext();
            
            c.put("user", user);
            this.getSession().set("loginTime", new Date());
            final Date now = new Date();
            this.getSession().setExpirationDate(
                new Date(now.getTime() + this.getServer().getSessionType()));
            
            // cookie to renew the session time out to count it from login time
            final HttpCookie renewTimeout = new HttpCookie(
                HttpServer.SESSION_ID_NAME, this.getSession().getId(), 
                this.getServer().sessionLiveTime() / 1000); 
            
            final LoginResult result = new LoginResult(true, "content/status", name);
            
            // get menu entries that the user is allowed to access
            for (final MenuEntry e : this.getHttpManager().getMenuEntries()) {
                if (this.getMyPolly().roles().canAccess(user, e)) {
                    result.menu.add(e);
                }
            }
            
            return new GsonHttpAnswer(200, result).addCookie(renewTimeout);
        }
        
        return new GsonHttpAnswer(200, new LoginResult(false, "", ""));
    }
    
    
    
    public final static class SessionTimeResult {
        public final String timeLeft;
        public final boolean loggedIn;
        public final String name;
        public SessionTimeResult(String name, String timeLeft, boolean loggedIn) {
            super();
            this.name = name;
            this.timeLeft = timeLeft;
            this.loggedIn = loggedIn;
        }
    }
    
    
    
    @Get("/api/checkLogin")
    public HttpAnswer getSessionTime() {
        final User user = this.getSessionUser();
        if (user != null) {
            final long now = new Date().getTime();
            final long start = ((Date) this.getSession().getAttached("loginTime")).getTime();
            final long diff = now - start;
            long tl = this.getServer().sessionLiveTime() - diff;
            //tl = (int) Math.ceil(tl / (double) 60000) * 60000; // round to minutes
            
            final Object result = new SessionTimeResult(user.getName(), 
                this.getMyPolly().formatting().formatTimeSpanMs(tl), true);
            
            return new GsonHttpAnswer(200, result);
        } else {
            return new GsonHttpAnswer(200, new SessionTimeResult("", "", false));
        }
    }
    
    
    
    @Get("api/updateMenu")
    public HttpAnswer getMenu() {
        return HttpAnswers.createTemplateAnswer("index.menu.html", this.createContext());
    }
    

    
    
    
    @Get("/api/logout")
    public HttpAnswer logout() {
        this.getSession().detach("user");
        this.getSession().kill();
        return HttpAnswers.createStringAnswer("").redirectTo("/");
    }
    
    
    
    @Get("content/status")
    public HttpAnswer status() {
        return HttpAnswers.createTemplateAnswer("templates/home/home.html", 
            this.createContext());
    }

    
    
    @Get("/")
    public HttpAnswer index() {
        final Map<String, Object> c = this.createContext();
        return this.makeAnswer(c);
    }
}
