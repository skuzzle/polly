package de.skuzzle.polly.core.internal.httpv2;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.http.annotations.Get;
import de.skuzzle.polly.http.annotations.OnRegister;
import de.skuzzle.polly.http.annotations.Param;
import de.skuzzle.polly.http.annotations.Post;
import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.HttpCookie;
import de.skuzzle.polly.http.api.HttpServer;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.httpv2.GsonHttpAnswer;
import de.skuzzle.polly.sdk.httpv2.PollyController;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.time.Milliseconds;

public class IndexController extends PollyController {

    
    
    public IndexController(MyPolly myPolly) {
        super(myPolly);
    }
    
    
    
    @Override
    protected Controller createInstance() {
        return new IndexController(this.getMyPolly());
    }
    
    
    
    @Get(STYLE_SHEET_NAME)
    public HttpAnswer getCSS() {
        return HttpAnswers.newTemplateAnswer(
            STYLE_SHEET_NAME, new HashMap<String, Object>());
    }
    

    
    
    public final static class LoginResult {
        public boolean success;
        public String contentPage;
        public String userName;
        
        public LoginResult(boolean success, String contentPage, String userName) {
            super();
            this.success = success;
            this.contentPage = contentPage;
            this.userName = userName;
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
            this.getSession().set("loginTime", new Date());
            
            final Date now = new Date();
            this.getSession().setExpirationDate(
                new Date(now.getTime() + this.getServer().getSessionType()));
            
            // cookie to renew the session time out to count it from login time
            final HttpCookie renewTimeout = new HttpCookie(
                HttpServer.SESSION_ID_NAME, this.getSession().getId(), 
                Milliseconds.toSeconds(this.getServer().sessionLiveTime()));
            
            final LoginResult result = new LoginResult(true, "content/status", name);
            return new GsonHttpAnswer(200, result).addCookie(renewTimeout);
        }
        
        return new GsonHttpAnswer(200, new LoginResult(false, "", ""));
    }
    
    
    
    
    
    
    public final static class SessionTimeResult {
        public final String timeLeft;
        public final boolean loggedIn;
        public final String userName;
        public SessionTimeResult(String name, String timeLeft, boolean loggedIn) {
            super();
            this.userName = name;
            this.timeLeft = timeLeft;
            this.loggedIn = loggedIn;
        }
    }
    
    
    
    @Get("/api/checkLogin")
    public HttpAnswer checkLogin() {
        final User user = this.getSessionUser();
        if (user != null) {
            final long now = new Date().getTime();
            final long start = ((Date) this.getSession().getAttached("loginTime")).getTime();
            final long diff = now - start;
            long tl = this.getServer().sessionLiveTime() - diff;
            tl = (int) Math.ceil(tl / (double) 60000) * 60000; // round to minutes
            
            final Object result = new SessionTimeResult(user.getName(), 
                this.getMyPolly().formatting().formatTimeSpanMs(tl), true);
            
            return new GsonHttpAnswer(200, result);
        } else {
            return new GsonHttpAnswer(200, new SessionTimeResult("", "", false));
        }
    }
    

    
    
    
    @Get("/api/logout")
    public HttpAnswer logout() {
        this.getSession().detach("user");
        this.getSession().kill();
        return HttpAnswers.newStringAnswer("").redirectTo("/");
    }

    
    
    @Get(value = "/", name = "Home")
    @OnRegister({ WebinterfaceManager.ADD_MENU_ENTRY, "General", 
        "Shows your polly home page" 
    })
    public HttpAnswer index() {
        final Map<String, Object> c = this.createContext("templatesv2/home.html");
        return this.makeAnswer(c);
    }
    
    
    
    @Get(value = "/pages/status", name = "Status")
    @OnRegister({WebinterfaceManager.ADD_MENU_ENTRY, 
        "Admin",
        "Shows polly status information", 
        RoleManager.ADMIN_PERMISSION
    })
    public HttpAnswer statusPage() throws AlternativeAnswerException {
        this.requirePermissions(RoleManager.ADMIN_PERMISSION);
        return this.makeAnswer(this.createContext("templatesv2/status.html"));
    }
}
