package polly.core.http.actions;



import polly.core.http.HttpInterface;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;
import de.skuzzle.polly.sdk.model.User;
import de.skuzzle.polly.sdk.roles.RoleManager;



public class UserInfoPageHttpAction extends HttpAction {

    
    public UserInfoPageHttpAction(MyPolly myPolly) {
        super("/user_info", myPolly);
    }
    
    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException {
        HttpTemplateContext context = new HttpTemplateContext(
            HttpInterface.PAGE_USER_INFO);
        context.put("formatter", this.myPolly.formatting());
        String userName = e.getProperty("userName");
        String action = e.getProperty("action");
        
        User u = this.myPolly.users().getUser(userName);
        context.put("user", u);
        context.put("action", action);
        boolean isHome = u != null && u.equals(e.getSession().getUser());
        boolean isAdmin = this.myPolly.roles().hasPermission(
                e.getSession().getUser(), RoleManager.ADMIN_PERMISSION);
        context.put("isHome", isHome);
        context.put("isAdmin", isAdmin);
        
        
        if (action != null && action.equals("update")) {
            if (!isHome && !isAdmin) {
                e.throwTemplateException("Invalid Request", 
                        "Your Request could not be executed");
            }
            for (String attribute : u.getAttributeNames()) {
                try {
                    this.myPolly.users().setAttributeFor(
                        u, attribute, e.getProperty(attribute));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } else if (action != null && action.equals("addRoleToUser")) {
            if (!isAdmin) {
                e.throwTemplateException("Invalid Request", 
                        "Your Request could not be executed");
            }
            
            String role = e.getProperty("role");
            User user = this.myPolly.users().getUser(userName);
            
            try {
                this.myPolly.roles().assignRole(user, role);
            } catch (RoleException e1) {
                e.throwTemplateException("Unknown role", 
                        e1.getMessage());
            } catch (DatabaseException e1) {
                e.throwTemplateException(e1);
            }
        } else if (action != null && action.equals("removeRole")) {
            if (!isAdmin) {
                e.throwTemplateException("Invalid Request", 
                        "Your Request could not be executed");
            }
            
            String role = e.getProperty("role");
            User user = this.myPolly.users().getUser(userName);
            
            try {
                this.myPolly.roles().removeRole(user, role);
            } catch (RoleException e1) {
                e.throwTemplateException("Unknown role", 
                        e1.getMessage());
            } catch (DatabaseException e1) {
                e.throwTemplateException(e1);
            }
        } else if (action != null && action.equals("setPassword")) {
            if (!isAdmin && !isHome) {
                e.throwTemplateException("Invalid Request", 
                        "Your Request could not be executed");
            }
            String newPassword = e.getProperty("password");
            
            if (newPassword == null || newPassword.equals("")) {
                e.throwTemplateException("Invalid password", 
                        "Password must not be empty");
            }
            
            PersistenceManager persistence = this.getMyPolly().persistence();
            try {
                persistence.writeLock();
                persistence.startTransaction();
                u.setPassword(newPassword);
                persistence.commitTransaction();
            } catch (DatabaseException e1) {
                e.throwTemplateException(e1);
            } finally {
                persistence.writeUnlock();
            }
            
        }
        
        context.put("roles", this.myPolly.roles().getRoles(u));
        context.put("allRoles", this.myPolly.roles().getRoles());
        
        return context;
    }

}
