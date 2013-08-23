package de.skuzzle.polly.core.internal.httpv2;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.httpv2.HttpManagerV2;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.time.Time;

public abstract class PollyController extends Controller {

    private final MyPolly myPolly;
    private final HttpManagerV2 httpManager;



    public PollyController(MyPolly myPolly, HttpManagerV2 httpManager) {
        this.myPolly = myPolly;
        this.httpManager = httpManager;
    }



    protected MyPolly getMyPolly() {
        return this.myPolly;
    }



    protected HttpManagerV2 getHttpManager() {
        return this.httpManager;
    }



    protected Map<String, Object> createContext(String contentTemplate) {
        final Map<String, Object> c = new HashMap<String, Object>();
        c.put("content", contentTemplate);
        c.put("myPolly", this.myPolly);
        c.put("user", this.getSessionUser());
        c.put("session", this.getSession());
        c.put("menu", this.httpManager.getMenuEntries());
        c.put("uptime", Time.currentTimeMillis()
            - this.getMyPolly().getStartTime().getTime());
        c.put("startTime", this.getMyPolly().getStartTime());
        c.put("sessionTimeOut", this.getEvent().getSource().sessionLiveTime());
        c.put("executionTime", 
            new Date().getTime() - this.getEvent().getEventTime().getTime());
        return c;
    }



    protected HttpAnswer makeAnswer(Map<String, Object> context) {
        return HttpAnswers.createTemplateAnswer("index.tmpl", context);
    }



    protected HttpAnswer makeAnswer(int responseCode, Map<String, Object> context) {
        return HttpAnswers.createTemplateAnswer(responseCode, "index.tmpl", context);
    }



    protected final User getSessionUser() {
        return (User) this.getSession().getAttached("user");
    }



    protected void requirePermissions(String... permissions)
            throws InsufficientRightsException {
        final User u = this.getSessionUser();
        final RoleManager rm = this.myPolly.roles();
        final Set<String> p = new HashSet<>(Arrays.asList(permissions));

        if (!rm.hasPermission(u, p)) {
            throw new InsufficientRightsException();
        }
    }
}
