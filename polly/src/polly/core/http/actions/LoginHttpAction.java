package polly.core.http.actions;

import polly.core.http.HttpInterface;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpManager;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.model.User;
import de.skuzzle.polly.sdk.roles.RoleManager;


public class LoginHttpAction extends HttpAction {

    private UserManager userManager;
    private RoleManager roleManager;
    
    
    
    public LoginHttpAction(MyPolly myPolly) {
        super("/login", myPolly);
        this.userManager = myPolly.users();
        this.roleManager = myPolly.roles();
    }
    
    

    @Override
    public HttpTemplateContext execute(HttpEvent e) {
        HttpTemplateContext context = new HttpTemplateContext(HttpInterface.PAGE_LOGIN);
        String userName = e.getProperty("userName");
        String password = e.getProperty("password");

        
        if (userName != null && password != null) {
            context.put("noAction", Boolean.FALSE);
            User u = this.userManager.getUser(userName);

            if (u != null && this.roleManager.hasPermission(
                        u, HttpManager.HTTP_ADMIN_PERMISSION)) {
                
                if (u.checkPassword(password)) {
                    e.getSession().setUser(u);
                    context.put("success", Boolean.TRUE);
                } else {
                    return e.getSource().errorTemplate("Login Error", 
                        "Invalid login data", e.getSession());
                }
            } else if (u != null) {
                return e.getSource().errorTemplate("Login Error", 
                    "Insufficient permissions", e.getSession());
            } else if (u == null) {
                return e.getSource().errorTemplate("Login Error", 
                    "Invalid login data", e.getSession());
            }
        } else {
            context.put("noAction", Boolean.TRUE);
        }
        return context;
    }

}
