package polly.core.http.actions;

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
    
    
    
    public LoginHttpAction(UserManager userManager, RoleManager roleManager) {
        super("/login");
        this.userManager = userManager;
        this.roleManager = roleManager;
    }
    
    

    @Override
    public void execute(HttpEvent e, HttpTemplateContext context) {
        String userName = e.getProperty("userName");
        String password = e.getProperty("password");
        context.setTemplate("webinterface/pages/login.html");
        
        if (userName != null && password != null) {
            context.put("noAction", Boolean.FALSE);
            User u = this.userManager.getUser(userName);

            if (u != null && this.roleManager.hasPermission(
                        u, HttpManager.LOGIN_PERMISSION)) {
                
                if (u.checkPassword(password)) {
                    e.getSession().setUser(u);
                    context.put("success", Boolean.TRUE);
                } else {
                    context.put("success", Boolean.FALSE);
                    context.put("reason", "Invalid login data");
                }
            } else if (u != null) {
                context.put("success", Boolean.FALSE);
                context.put("reason", "Invalid user level");
            } else if (u == null) {
                context.put("success", Boolean.FALSE);
                context.put("reason", "Invalid user data");
            }
        } else {
            context.put("noAction", Boolean.TRUE);
        }
        
    }

}
