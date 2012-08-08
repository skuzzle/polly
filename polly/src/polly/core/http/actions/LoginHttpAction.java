package polly.core.http.actions;

import polly.core.http.HttpInterface;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;
import de.skuzzle.polly.sdk.model.User;



public class LoginHttpAction extends HttpAction {

    private UserManager userManager;
    
    
    
    public LoginHttpAction(MyPolly myPolly) {
        super("/login", myPolly);
        this.userManager = myPolly.users();
    }
    
    

    @Override
    public HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException {
        HttpTemplateContext context = new HttpTemplateContext(HttpInterface.PAGE_LOGIN);
        String userName = e.getProperty("userName");
        String password = e.getProperty("password");

        
        if (userName != null && password != null) {
            context.put("noAction", Boolean.FALSE);
            User u = this.userManager.getUser(userName);

            if (u != null) {
                if (u.checkPassword(password)) {
                    e.getSession().setUser(u);
                    context.put("success", Boolean.TRUE);
                } else {
                    e.throwTemplateException("Login Error", 
                        "Invalid login data");
                }
            } else {
                e.throwTemplateException("Login Error", 
                    "Invalid login data");
            }
        } else {
            context.put("noAction", Boolean.TRUE);
        }
        return context;
    }

}
