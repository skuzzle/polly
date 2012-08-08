package polly.core.http.actions;



import polly.core.http.HttpInterface;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.http.AbstractAdminAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.model.User;


public class UserInfoPageHttpAction extends AbstractAdminAction {

    
    public UserInfoPageHttpAction(MyPolly myPolly) {
        super("/user_info", myPolly);
    }
    
    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e) {
        HttpTemplateContext context = new HttpTemplateContext(
            HttpInterface.PAGE_USER_INFO);
        context.put("formatter", this.myPolly.formatting());
        String userName = e.getProperty("userName");
        String action = e.getProperty("action");
        
        User u = this.myPolly.users().getUser(userName);
        context.put("user", u);
        context.put("action", action);
        
        if (action != null && action.equals("update")) {
            for (String attribute : u.getAttributeNames()) {
                try {
                    this.myPolly.users().setAttributeFor(
                        u, attribute, e.getProperty(attribute));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } else if (action != null && action.equals("addRoleToUser")) {
            String role = e.getProperty("role");
            User user = this.myPolly.users().getUser(userName);
            
            try {
                this.myPolly.roles().assignRole(user, role);
            } catch (RoleException e1) {
                return e.getSource().errorTemplate("Unknown role", 
                        e1.getMessage(), e.getSession());
            } catch (DatabaseException e1) {
                return e.getSource().errorTemplate("Database Error", 
                        e1.getMessage(), e.getSession());
            }
        } else if (action != null && action.equals("removeRole")) {
            String role = e.getProperty("role");
            User user = this.myPolly.users().getUser(userName);
            
            try {
                this.myPolly.roles().removeRole(user, role);
            } catch (RoleException e1) {
                return e.getSource().errorTemplate("Unknown role", 
                        e1.getMessage(), e.getSession());
            } catch (DatabaseException e1) {
                return e.getSource().errorTemplate("Database Error", 
                        e1.getMessage(), e.getSession());
            }
        } else if (action != null && action.equals("setPassword")) {
            String newPassword = e.getProperty("password");
            
            if (newPassword == null || newPassword.equals("")) {
                return e.getSource().errorTemplate("Invalid password", 
                        "Password must not be empty", e.getSession());
            }
            
            PersistenceManager persistence = this.getMyPolly().persistence();
            try {
                persistence.writeLock();
                persistence.startTransaction();
                u.setPassword(newPassword);
                persistence.commitTransaction();
            } catch (DatabaseException e1) {
                return e.getSource().errorTemplate("Database Error", 
                        e1.getMessage(), e.getSession());
            } finally {
                persistence.writeUnlock();
            }
            
        }
        
        context.put("roles", this.myPolly.roles().getRoles(u));
        context.put("allRoles", this.myPolly.roles().getRoles());
        
        return context;
    }

}
