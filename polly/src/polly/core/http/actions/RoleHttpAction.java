package polly.core.http.actions;

import java.util.Set;
import java.util.TreeSet;

import polly.core.http.HttpInterface;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.roles.RoleManager;

public class RoleHttpAction extends HttpAction {

    private RoleManager roleManager;
    
    
    public RoleHttpAction(RoleManager roleManager) {
        super("/Roles");
        this.roleManager = roleManager;
    }

    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e) {
        HttpTemplateContext c = new HttpTemplateContext(HttpInterface.PAGE_ROLES);
        
        String action = e.getProperty("action");
        
        if (action != null && action.equals("addPermissionToRole")) {
            String role = e.getProperty("role");
            String permission = e.getProperty("permission");
            
            try {
                this.roleManager.assignPermission(role, permission);
            } catch (DatabaseException e1) {
                return e.getSource().errorTemplate("Database Error", 
                        e1.getMessage(), e.getSession());
            } catch (RoleException e1) {
                return e.getSource().errorTemplate("Unknown permission", 
                        e1.getMessage(), e.getSession());
            }
        } else if (action != null && action.equals("removePermissionFromRole")) {
            String role = e.getProperty("role");
            String permission = e.getProperty("permission");
            
            try {
                this.roleManager.removePermission(role, permission);
            } catch (RoleException e1) {
                return e.getSource().errorTemplate("Unknown role", 
                        e1.getMessage(), e.getSession());
            } catch (DatabaseException e1) {
                return e.getSource().errorTemplate("Database Error", 
                        e1.getMessage(), e.getSession());
            }
        }
        
        Set<String> allPerms = new TreeSet<String>();
        for (String role : this.roleManager.getRoles()) {
            allPerms.addAll(this.roleManager.getPermissions(role));
        }
        
        c.put("allPerms", allPerms);
        c.put("rolemanager", this.roleManager);
        c.put("roles", this.roleManager.getRoles());
        
        return c;
    }
}
