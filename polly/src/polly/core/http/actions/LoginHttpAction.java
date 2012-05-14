package polly.core.http.actions;

import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.http.AbstractHttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.model.User;


public class LoginHttpAction extends AbstractHttpAction {

    private UserManager userManager;
    
    
    public LoginHttpAction(UserManager userManager) {
        super("/login");
        this.userManager = userManager;
    }
    
    

    @Override
    public void execute(HttpEvent e, HttpTemplateContext context) {
        String userName = e.getProperty("userName");
        String password = e.getProperty("password");
        context.setResultUrl("webinterface/pages/login.html");
        
        if (userName != null && password != null) {
            context.put("noAction", Boolean.FALSE);
            User u = this.userManager.getUser(userName);
            
            if (u != null && u.checkPassword(password)) {
                e.getSession().setUser(u);
                context.setResultUrl("webinterface/pages/home.html");
            } else {
                context.put("success", Boolean.FALSE);
            }
        } else {
            context.put("noAction", Boolean.TRUE);
        }
        
    }

}
