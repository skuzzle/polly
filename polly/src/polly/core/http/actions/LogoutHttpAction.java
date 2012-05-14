package polly.core.http.actions;

import de.skuzzle.polly.sdk.http.AbstractHttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;


public class LogoutHttpAction extends AbstractHttpAction {

    public LogoutHttpAction() {
        super("/logout");
    }
    
    
    
    @Override
    public void execute(HttpEvent e, HttpTemplateContext context) {
        context.setResultUrl("webinterface/pages/home.html");
        e.getSession().setUser(null);
    }

}
