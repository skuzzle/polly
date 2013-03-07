package polly.core.http.actions;

import org.apache.log4j.Logger;

import polly.core.http.HttpInterface;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.http.Cookie;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;


public class LogoutHttpAction extends HttpAction {

    private final static Logger logger = Logger
        .getLogger(LogoutHttpAction.class.getName());
    
    public LogoutHttpAction(MyPolly myPolly) {
        super("/logout", myPolly);
    }
    
    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException {
        if (e.getSession().isLoggedIn()) {
            logger.info("HTTP logout: " + e.getSession().getUser());
            HttpTemplateContext context = new HttpTemplateContext(HttpInterface.PAGE_HOME);
            e.getSource().closeSession(e.getSession());
            
            context.setCookie(new Cookie("sessionid", e.getSession().getId(), 0));
            return context;
        } else {
            e.throwTemplateException("You are not logged in", 
                "You can only logout if you previously logged in.");
            
            // XXX: not reachable
            return null;
        }
    }

}
