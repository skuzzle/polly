package polly.core.http.actions;

import java.util.List;

import polly.core.http.HttpInterface;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.exceptions.UserExistsException;
import de.skuzzle.polly.sdk.http.AbstractAdminAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.model.User;



public class UserPageHttpAction extends AbstractAdminAction {

    
    public UserPageHttpAction(MyPolly myPolly) {
        super("/Users", myPolly);
    }
    
    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e) {
        HttpTemplateContext context = new HttpTemplateContext(HttpInterface.PAGE_USERS);
        context.put("formatter", this.myPolly.formatting());
        String action = e.getProperty("action");
        
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
            
        } else if (action != null && action.equals("addUser")) {
            String newName = e.getProperty("newname");
            String password = e.getProperty("password");
            String[] roles = e.getProperty("roles") != null
                    ? e.getProperty("roles").split(";") 
                    : new String[0];
            
            if (password == null || password.equals("")) {
                return e.getSource().errorTemplate("Invalid password", 
                        "Password must not be empty", e.getSession());
            } else if (newName == null || newName.equals("")) {
                return e.getSource().errorTemplate("Invalid user name", 
                        "User name must not be empty", e.getSession());
            }
            
            UserManager userManager = this.myPolly.users();
            
            try {
                User newUser = userManager.addUser(newName, password);
                for (String role : roles) {
                    this.myPolly.roles().assignRole(newUser, role);
                }
            } catch (UserExistsException e1) {
                return e.getSource().errorTemplate("Can not add user", 
                        "User with that name already exists", e.getSession());
            } catch (DatabaseException e1) {
                return e.getSource().errorTemplate("Database Error", 
                        e1.getMessage(), e.getSession());
            } catch (RoleException e1) {
                return e.getSource().errorTemplate("Unknown role", 
                        e1.getMessage(), e.getSession());
            }
        }
        
        List<User> users = this.myPolly.users().getRegisteredUsers();
        context.put("users", users);
        
        return context;
    }
}
