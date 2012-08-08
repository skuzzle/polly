package polly.core.http.actions;

import polly.core.http.HttpInterface;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;


public class LogoutHttpAction extends HttpAction {

    public LogoutHttpAction(MyPolly myPolly) {
        super("/logout", myPolly);
    }
    
    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e) {
        HttpTemplateContext context = new HttpTemplateContext(HttpInterface.PAGE_HOME);
        e.getSource().closeSession(e.getSession());
        return context;
    }

}
