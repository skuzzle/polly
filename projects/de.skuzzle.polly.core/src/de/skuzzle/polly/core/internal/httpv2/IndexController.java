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
import de.skuzzle.polly.http.api.HttpException;
import de.skuzzle.polly.http.api.HttpServer;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.httpv2.GsonHttpAnswer;
import de.skuzzle.polly.sdk.httpv2.PollyController;
import de.skuzzle.polly.sdk.httpv2.SuccessResult;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.time.Milliseconds;
import de.skuzzle.polly.tools.concurrent.RunLater;

public class IndexController extends PollyController {
    
    
    private final static String ADMIN_CATEGORY_KEY = "indexAdminCategory"; //$NON-NLS-1$
    private final static String GENERAL_CATEGORY_KEY = "indexGeneralCategory"; //$NON-NLS-1$
    private final static String HOME_NAME_KEY = "indexHomePage"; //$NON-NLS-1$
    private final static String HOME_DESC_KEY = "indexHomeDesc"; //$NON-NLS-1$
    private final static String STATUS_NAME_KEY = "indexStatusPage"; //$NON-NLS-1$
    private final static String STATUS_DESC_KEY = "indexStatusDesc"; //$NON-NLS-1$
    
    public final static String PAGE_INDEX = "/"; //$NON-NLS-1$
    public final static String PAGE_STATUS = "/pages/status"; //$NON-NLS-1$
    
    private final static String CONTENT_INDEX = "templatesv2/home.html"; //$NON-NLS-1$
    private static final String CONTENT_STATUS = "templatesv2/status.html"; //$NON-NLS-1$
    
    public final static String API_LOGIN = "/api/login"; //$NON-NLS-1$
    public final static String API_CHECK_LOGIN = "/api/checkLogin";  //$NON-NLS-1$
    public final static String API_LOGOUT = "/api/logout"; //$NON-NLS-1$
    public final static String API_CALC_EXPRESSION = "/api/calculateExpression"; //$NON-NLS-1$
    public final static String API_RUN_GC = "/api/runGC"; //$NON-NLS-1$
    public final static String API_SHUTDOWN = "/api/shutdown"; //$NON-NLS-1$
    public final static String API_ADD_NEWS = "/api/postNews"; //$NON-NLS-1$
    public final static String API_DELETE_NEWS = "/api/deleteNews"; //$NON-NLS-1$
    
    private final NewsManager newsManager;
    
    
    public IndexController(MyPolly myPolly, NewsManager newsManager) {
        super(myPolly);
        this.newsManager = newsManager;
    }
    
    
    
    @Override
    protected Controller createInstance() {
        return new IndexController(this.getMyPolly(), this.newsManager);
    }
    
    
    
    @Get(STYLE_SHEET_NAME)
    public HttpAnswer getCSS() {
        return HttpAnswers.newTemplateAnswer(
            STYLE_SHEET_NAME, new HashMap<String, Object>());
    }
    

    
    
    public final static class LoginResult {
        public boolean success;
        public String userName;
        
        public LoginResult(boolean success, String userName) {
            super();
            this.success = success;
            this.userName = userName;
        }
    }
    
    
    
    @Post(API_LOGIN)
    public HttpAnswer login(
        @Param("name") String name, 
        @Param("pw") String pw) {
        
        final UserManager um = this.getMyPolly().users();
        final User user = um.getUser(name);
        
        if (user != null && user.checkPassword(pw)) {
            this.getSession().set(WebinterfaceManager.USER, user);
            this.getSession().set(WebinterfaceManager.LOGIN_TIME, new Date());
            
            final Date now = new Date();
            this.getSession().setExpirationDate(
                new Date(now.getTime() + this.getServer().getSessionType()));
            
            // cookie to renew the session time out to count it from login time
            final HttpCookie renewTimeout = new HttpCookie(
                HttpServer.SESSION_ID_NAME, this.getSession().getId(), 
                Milliseconds.toSeconds(this.getServer().sessionLiveTime()));
            
            final LoginResult result = new LoginResult(true, name);
            return new GsonHttpAnswer(200, result).addCookie(renewTimeout);
        }
        
        return new GsonHttpAnswer(200, new LoginResult(false, "")); //$NON-NLS-1$
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
    
    
    
    @Get(API_CHECK_LOGIN)
    public HttpAnswer checkLogin() {
        final User user = this.getSessionUser();
        if (user != null) {
            final long now = new Date().getTime();
            final long start = ((Date) this.getSession().getAttached(
                    WebinterfaceManager.LOGIN_TIME)).getTime();
            final long diff = now - start;
            long tl = this.getServer().sessionLiveTime() - diff;
            tl = (int) Math.ceil(tl / (double) 60000) * 60000; // round to minutes
            
            final Object result = new SessionTimeResult(user.getName(), 
                this.getMyPolly().formatting().formatTimeSpanMs(tl), true);
            
            return new GsonHttpAnswer(200, result);
        } else {
            return new GsonHttpAnswer(200, new SessionTimeResult("", "", false)); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    

    
    
    
    @Get(API_LOGOUT)
    public HttpAnswer logout() {
        this.getSession().detach(WebinterfaceManager.USER);
        this.getSession().kill();
        return HttpAnswers.newRedirectAnswer(PAGE_INDEX);
    }

    
    
    @Get(value = PAGE_INDEX, name = HOME_NAME_KEY)
    @OnRegister({ 
        WebinterfaceManager.ADD_MENU_ENTRY, 
        MSG.FAMILY,
        GENERAL_CATEGORY_KEY, 
        HOME_DESC_KEY
    })
    public HttpAnswer index() {
        final Map<String, Object> c = this.createContext(CONTENT_INDEX);
        c.put("allNews", this.newsManager.getAllNews()); //$NON-NLS-1$
        c.put("commits", GitHubController.getLatestCommits()); //$NON-NLS-1$
        c.put("lastRefresh", GitHubController.getLastRefresh()); //$NON-NLS-1$
        return this.makeAnswer(c);
    }
    
    
    
    
    @Get(API_CALC_EXPRESSION)
    public HttpAnswer calculateExpression(
            @Param(value = "expr", treatEmpty = true) String expression) {
        
        final Types result = this.getMyPolly().parse(expression);
        return new GsonHttpAnswer(200, 
            new SuccessResult(true, result.valueString(this.getMyPolly().formatting())));
    }
    
    
    
    @Get(value = PAGE_STATUS, name = STATUS_NAME_KEY)
    @OnRegister({
        WebinterfaceManager.ADD_MENU_ENTRY, 
        MSG.FAMILY,
        ADMIN_CATEGORY_KEY,
        STATUS_DESC_KEY, 
        RoleManager.ADMIN_PERMISSION
    })
    public HttpAnswer statusPage() throws AlternativeAnswerException {
        this.requirePermissions(RoleManager.ADMIN_PERMISSION);
        final Map<String, Object> c = this.createContext(CONTENT_STATUS);
        c.put("maxMemory", Runtime.getRuntime().maxMemory()); //$NON-NLS-1$
        c.put("totalMemory", Runtime.getRuntime().totalMemory()); //$NON-NLS-1$
        c.put("freeMemory", Runtime.getRuntime().freeMemory()); //$NON-NLS-1$
        return this.makeAnswer(c);
    }
    
    
    
    @Get(API_RUN_GC)
    public HttpAnswer runGC() throws AlternativeAnswerException {
        this.requirePermissions(RoleManager.ADMIN_PERMISSION);
        System.gc();
        return HttpAnswers.newRedirectAnswer(PAGE_STATUS);
    }
    
    
    
    @Post(API_ADD_NEWS)
    public HttpAnswer postNews(
            @Param("caption") String caption, 
            @Param("body") String body) throws HttpException {
        
        this.requirePermissions(NewsManager.ADD_NEWS_PERMISSION);
        
        try {
            this.newsManager.addNewsEntry(this.getSessionUser(), caption, body);
            return HttpAnswers.newRedirectAnswer(PAGE_INDEX);
        } catch (DatabaseException e) {
            throw new HttpException(e);
        }
    }
    
    
    
    @Get(API_DELETE_NEWS)
    public HttpAnswer deleteNews(@Param("newsId") int newsId) throws HttpException {
        this.requirePermissions(NewsManager.DELETE_NEWS_PERMISSION);
        
        try {
            this.newsManager.deleteNewsEntry(newsId);
            return HttpAnswers.newRedirectAnswer(PAGE_INDEX);
        } catch (DatabaseException e) {
            throw new HttpException(e);
        }
    }
    
    
    
    @Get(API_SHUTDOWN)
    public HttpAnswer shutdownPolly(@Param("restart") final Boolean restart) 
            throws AlternativeAnswerException {
        this.requirePermissions(RoleManager.ADMIN_PERMISSION);
        final int shutdownTimeSeconds = 10;
        new RunLater("SHUTDOWN_TIMER", Milliseconds.fromSeconds(shutdownTimeSeconds)) { //$NON-NLS-1$
            @Override
            public void run() {
                if (restart) {
                    getMyPolly().shutdownManager().restart();
                } else {
                    getMyPolly().shutdownManager().shutdown();
                }
            }
        }.start();
        final String s = restart ? MSG.indexRestart : MSG.indexShutdown;
        return HttpAnswers.newStringAnswer(MSG.bind(s, shutdownTimeSeconds));
    }
}
