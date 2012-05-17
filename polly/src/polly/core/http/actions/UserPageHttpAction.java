package polly.core.http.actions;

import java.util.List;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.model.User;


public class UserPageHttpAction extends HttpAction {

    private MyPolly myPolly;
    
    
    public UserPageHttpAction(MyPolly myPolly) {
        super("/Users");
        this.myPolly = myPolly;
    }
    
    
    
    @Override
    public void execute(HttpEvent e, HttpTemplateContext context) {
        context.setTemplate("webinterface/pages/users.html");
        context.put("formatter", this.myPolly.formatting());
        String action = e.getProperty("action");
        
        if (e.getSession().isLoggedIn()) {
            if (action != null && action.equals("delete")) {
                String userName = e.getProperty("userName");
                User user = this.myPolly.users().getUser(userName);
                
                if (user != null && !user.equals(this.myPolly.users().getAdmin())) {
                    try {
                        this.myPolly.users().deleteUser(user);
                    } catch (UnknownUserException e1) {
                        e1.printStackTrace();
                    } catch (DatabaseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            
            List<User> users = this.myPolly.users().getRegisteredUsers();
            context.put("users", users);
        }
    }

}
