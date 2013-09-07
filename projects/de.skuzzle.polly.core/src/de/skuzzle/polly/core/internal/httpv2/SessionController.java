package de.skuzzle.polly.core.internal.httpv2;

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
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.roles.RoleManager;


public class SessionController extends PollyController {

    
    
    public SessionController(MyPolly myPolly, String rootDir, 
            WebinterfaceManager httpManager) {
        super(myPolly, rootDir, httpManager);
    }
    
    

    @Override
    protected Controller createInstance() {
        return new SessionController(this.getMyPolly(), this.getRootDir(), 
            this.getHttpManager());
    }

    
    
    @Get(value = "/pages/sessions", name = "Sessions")
    @OnRegister({
        WebinterfaceManager.ADD_MENU_ENTRY, 
        "Admin", "List and manage currently active HTTP sessions",
        RoleManager.ADMIN_PERMISSION
    })
    public HttpAnswer sessions() throws AlternativeAnswerException {
        this.requirePermissions(RoleManager.ADMIN_PERMISSION);
        final Map<String, Object> c = this.createContext("templatesv2/sessions.html");
        c.put("allSessions", this.getHttpManager().getServer().getSessions());
        
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
        
        return HttpAnswers.newTemplateAnswer(
            this.resolveFile("templatesv2/session.events.html"),
            "myPolly", this.getMyPolly(),
            "ss", session);
    }
}
