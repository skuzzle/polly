package de.skuzzle.polly.core.internal.httpv2;

import java.util.Map;

import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.Get;
import de.skuzzle.polly.http.api.HttpSession;
import de.skuzzle.polly.http.api.Param;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.httpv2.HttpManagerV2;


public class SessionController extends PollyController {

    
    
    public SessionController(MyPolly myPolly, HttpManagerV2 httpManager) {
        super(myPolly, httpManager);
    }
    
    

    @Override
    protected Controller createInstance() {
        return new SessionController(this.getMyPolly(), this.getHttpManager());
    }

    
    
    @Get("/content/sessions")
    public HttpAnswer index() throws InsufficientRightsException {
        this.requirePermissions(HttpManagerV2.HTTP_ADMIN_PERMISSION);
        final Map<String, Object> c = this.createContext();
        c.put("allSessions", this.getHttpManager().getServer().getSessions());
        
        return HttpAnswers.createTemplateAnswer("templates/sessions/sessions.html", c);
    }
    
    
    
    @Get("/api/getEvents")
    public HttpAnswer listSessions(@Param("id") String id) 
            throws InsufficientRightsException {
        this.requirePermissions(HttpManagerV2.HTTP_ADMIN_PERMISSION);
        
        final HttpSession session = this.getServer().findSession(id);
        
        return HttpAnswers.createTemplateAnswer("templates/sessions/session.events.html", 
            "myPolly", this.getMyPolly(),
            "ss", session);
    }
}
