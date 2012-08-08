package polly.core.http.actions;

import polly.core.http.HttpInterface;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.http.AbstractAdminAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpSession;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;



public class SessionPageHttpAction extends AbstractAdminAction {

    
    public SessionPageHttpAction(MyPolly myPolly) {
        super("/Sessions", myPolly);
    }
    
    

    @Override
    public HttpTemplateContext execute(HttpEvent e)
            throws HttpTemplateException {

        HttpTemplateContext c = new HttpTemplateContext(HttpInterface.PAGE_SESSIONS);
        
        String action = e.getProperty("action");
        
        if (action != null && action.equals("killSession")) {
            String sessionId = e.getProperty("sessionId");

            HttpSession session = e.getSource().findSession(sessionId);
            if (session == null) {
                e.throwTemplateException("Invalid Session Id", 
                        "No Session with ID " + sessionId);
            }
            e.getSource().closeSession(session);
        }
        
        return c;
    }

}
