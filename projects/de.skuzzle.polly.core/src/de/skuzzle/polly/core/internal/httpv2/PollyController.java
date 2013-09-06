package de.skuzzle.polly.core.internal.httpv2;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.time.Time;

public abstract class PollyController extends Controller {

    protected final static String STYLE_SHEET_NAME = "template.css";
    protected final static String PAGE_PREFIX = "PAGE_";
    
    
    private final MyPolly myPolly;
    private final WebinterfaceManager httpManager;



    public PollyController(MyPolly myPolly, WebinterfaceManager httpManager) {
        this.myPolly = myPolly;
        this.httpManager = httpManager;
        this.setHandlerPrefix(PAGE_PREFIX);
    }
    
    
    
    @Override
    public void putHandledURL(Map<String, String> target, String handlerName,
            String url) {
        handlerName = handlerName.replaceAll("\\s+", "_").toUpperCase();
        super.putHandledURL(target, handlerName, url);
    }

    

    protected MyPolly getMyPolly() {
        return this.myPolly;
    }



    protected WebinterfaceManager getHttpManager() {
        return this.httpManager;
    }



    protected Map<String, Object> createContext(String content) {
        final Map<String, Object> c = new HashMap<String, Object>();
        
        c.put("myPolly", this.myPolly);
        c.put("httpServer", this.getServer());
        c.put("user", this.getSessionUser());
        c.put("session", this.getSession());
        c.put("menu", this.httpManager.getMenuCategories());
        c.put("topMenu", this.httpManager.getTopMenuEntries());
        c.put("uptime", Time.currentTimeMillis()
            - this.getMyPolly().getStartTime().getTime());
        c.put("startTime", this.getMyPolly().getStartTime());
        c.put("sessionTimeOut", this.getEvent().getSource().sessionLiveTime());
        c.put("content", content);
        c.put("executionTime", 
            new Date().getTime() - this.getEvent().getEventTime().getTime());
        c.putAll(this.getServer().getHandledUrls());
        return c;
    }
    


    protected HttpAnswer makeAnswer(Map<String, Object> context) {
        return HttpAnswers.createTemplateAnswer("template.html", context);
    }



    protected HttpAnswer makeAnswer(int responseCode, Map<String, Object> context) {
        return HttpAnswers.createTemplateAnswer(responseCode, "template.html", context);
    }



    protected final User getSessionUser() {
        return (User) this.getSession().getAttached("user");
    }



    protected void requirePermissions(String... permissions) 
            throws AlternativeAnswerException {
        final User u = this.getSessionUser();
        final RoleManager rm = this.myPolly.roles();
        final Set<String> p = new HashSet<>(Arrays.asList(permissions));

        if (!rm.hasPermission(u, p)) {
            final Map<String, Object> c = this.createContext("templatesv2/no_permission.html");
            c.put("permissions", p);
            c.put("resource", this.getEvent().getPlainUri());
            throw new AlternativeAnswerException(this.makeAnswer(c));
        }
    }
}
