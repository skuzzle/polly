package polly.core.http.actions;

import java.util.List;

import polly.core.http.HttpInterface;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.model.User;



public class UserPageHttpAction extends AbstractAdminAction {

    private MyPolly myPolly;
    
    
    public UserPageHttpAction(MyPolly myPolly) {
        super("/Users");
        this.myPolly = myPolly;
    }
    
    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e) {
        HttpTemplateContext context = new HttpTemplateContext(HttpInterface.PAGE_USERS);
        context.put("formatter", this.myPolly.formatting());
        String action = e.getProperty("action");
        
        if (e.getSession().isLoggedIn()) {
            if (action != null && action.equals("delete")) {
                String userName = e.getProperty("userName");
                User user = this.myPolly.users().getUser(userName);
                
                if (user != null && !user.equals(this.myPolly.users().getAdmin())) {
                    try {
                        this.myPolly.users().deleteUser(user);
                    } catch (Exception ex) {
                        return e.getSource().errorTemplate("Unexpected exception", 
                            "An unexpected exception occurred while processing " +
                            "your request: " + ex.getMessage(), e.getSession());
                    }
                } else {
                    return e.getSource().errorTemplate("User can not be deleted", 
                        "This user is protected and can not be deleted!", e.getSession());
                }
            }
            
            List<User> users = this.myPolly.users().getRegisteredUsers();
            context.put("users", users);
        }
        return context;
    }
}
