package de.skuzzle.polly.sdk.httpv2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.sdk.Messages;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.httpv2.html.HTMLTools;
import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.time.Time;

public abstract class PollyController extends Controller {
    

    protected final static String STYLE_SHEET_NAME = "template.css"; //$NON-NLS-1$
    protected final static String PAGE_PREFIX = "PAGE_"; //$NON-NLS-1$
    
    
    private final MyPolly myPolly;


    public PollyController(MyPolly myPolly) {
        this.myPolly = myPolly;
        this.setHandlerPrefix(PAGE_PREFIX);
    }
    
    
    
    @Override
    public void putHandledURL(Map<String, String> target, String handlerName,
            String url) {
        handlerName = handlerName.replaceAll("\\s+", "_").toUpperCase(); //$NON-NLS-1$ //$NON-NLS-2$
        super.putHandledURL(target, handlerName, url);
    }

    

    protected MyPolly getMyPolly() {
        return this.myPolly;
    }



    protected Map<String, Object> createContext(String content) {
        final Map<String, Object> c = new HashMap<String, Object>();
        
        c.put("myPolly", this.myPolly); //$NON-NLS-1$
        c.put("httpServer", this.getServer()); //$NON-NLS-1$
        c.put("user", this.getSessionUser()); //$NON-NLS-1$
        c.put("session", this.getSession()); //$NON-NLS-1$
        c.put("menu", this.myPolly.webInterface().getMenuCategories()); //$NON-NLS-1$
        c.put("topMenu", this.myPolly.webInterface().getTopMenuEntries()); //$NON-NLS-1$
        c.put("uptime", Time.currentTimeMillis() //$NON-NLS-1$
            - this.getMyPolly().getStartTime().getTime());
        c.put("startTime", this.getMyPolly().getStartTime()); //$NON-NLS-1$
        c.put("sessionTimeOut", this.getEvent().getSource().sessionLiveTime()); //$NON-NLS-1$
        c.put("content", content); //$NON-NLS-1$
        c.put("executionTime",  //$NON-NLS-1$
            Time.currentTimeMillis() - this.getEvent().getEventTime().getTime());
        c.put("Constants", Constants.class); //$NON-NLS-1$
        c.put("HTML", HTMLTools.class); //$NON-NLS-1$
        c.putAll(this.getServer().getHandledUrls());
        return c;
    }
    


    protected HttpAnswer makeAnswer(Map<String, Object> context) {
        return HttpAnswers.newTemplateAnswer("template.html", context); //$NON-NLS-1$
    }



    protected HttpAnswer makeAnswer(int responseCode, Map<String, Object> context) {
        return HttpAnswers.newTemplateAnswer(responseCode, "template.html", context); //$NON-NLS-1$
    }



    protected final User getSessionUser() {
        return (User) this.getSession().get(WebinterfaceManager.USER);
    }



    protected void requirePermissions(String... permissions) 
            throws AlternativeAnswerException {
        final User u = this.getSessionUser();
        final RoleManager rm = this.myPolly.roles();
        final Set<String> p = new HashSet<>(Arrays.asList(permissions));

        if (!rm.hasPermission(u, p)) {
            final Map<String, Object> c = this.createContext(
                "templatesv2/no_permission.html"); //$NON-NLS-1$
            c.put("permissions", p); //$NON-NLS-1$
            c.put("resource", this.getEvent().getPlainUri()); //$NON-NLS-1$
            throw new AlternativeAnswerException(this.makeAnswer(c));
        }
    }
    
    
    
    protected User checkLogin(String userName, String pwHash) 
            throws AlternativeAnswerException {
        final User u = this.getMyPolly().users().getUser(userName);
        if (u == null || !u.getHashedPassword().equalsIgnoreCase(pwHash)) {
            throw new AlternativeAnswerException(new GsonHttpAnswer(200, 
                    new SuccessResult(false, Messages.illegalLogin)));
        }
        return u;
    }
}
