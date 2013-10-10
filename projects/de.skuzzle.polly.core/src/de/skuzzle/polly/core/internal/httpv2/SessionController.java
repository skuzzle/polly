package de.skuzzle.polly.core.internal.httpv2;

import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.http.annotations.Get;
import de.skuzzle.polly.http.annotations.OnRegister;
import de.skuzzle.polly.http.annotations.Param;
import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.HttpSession;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.httpv2.PollyController;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.roles.RoleManager;


public class SessionController extends PollyController {

    private final static String ADMIN_CATEGORY_KEY = "sessionAdminCategory"; //$NON-NLS-1$
    private final static String SESSION_NAME_KEY = "sessionManagerPage"; //$NON-NLS-1$
    private final static String SESSION_DESC_KEY = "sessionManagerDesc"; //$NON-NLS-1$
    
    
    public SessionController(MyPolly myPolly) {
        super(myPolly);
    }
    
    

    @Override
    protected Controller createInstance() {
        return new SessionController(this.getMyPolly());
    }

    
    
    @Get(value = "/pages/sessions", name = SESSION_NAME_KEY)
    @OnRegister({
        WebinterfaceManager.ADD_MENU_ENTRY, 
        MSG.FAMILY,
        ADMIN_CATEGORY_KEY,
        SESSION_DESC_KEY,
        RoleManager.ADMIN_PERMISSION
    })
    public HttpAnswer sessions() throws AlternativeAnswerException {
        this.requirePermissions(RoleManager.ADMIN_PERMISSION);
        final Map<String, Object> c = this.createContext("templatesv2/sessions.html");
        c.put("allSessions", this.getMyPolly().webInterface().getServer().getSessions());
        
        return this.makeAnswer(c);
    }
    
    
    
    @Get("/api/killSession")
    public HttpAnswer killSession(@Param("id") String id) {
        final HttpSession session = this.getServer().findSession(id);
        if (session != null) {
            session.kill();
            return HttpAnswers.newStringAnswer("success");
        }
        return HttpAnswers.newStringAnswer("fail");
    }
    
    
    @Get("/api/detach")
    public HttpAnswer detachItem(@Param("id") String id, @Param("key") String key) {
        final HttpSession session = this.getServer().findSession(id);
        if (session != null) {
            session.detach(key);
            return HttpAnswers.newStringAnswer("success");
        }
        return HttpAnswers.newStringAnswer("fail");
    }
    
    
    
    @Get("/api/getEvents")
    public HttpAnswer listSessions(@Param("id") String id) 
            throws AlternativeAnswerException {
        
        if (!this.getMyPolly().roles().hasPermission(
                this.getSessionUser(), RoleManager.ADMIN_PERMISSION)) {
            
            return HttpAnswers.newStringAnswer("Required permission: " + 
                RoleManager.ADMIN_PERMISSION);
        }
        
        final HttpSession session = this.getServer().findSession(id);
        final Map<String, Object> c = new HashMap<>();
        c.put("myPolly", this.getMyPolly());
        c.put("ss", session);
        
        return HttpAnswers.newTemplateAnswer("templatesv2/session.events.html", c);
    }
}
