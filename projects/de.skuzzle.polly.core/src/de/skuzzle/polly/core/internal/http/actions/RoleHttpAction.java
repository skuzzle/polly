package de.skuzzle.polly.core.internal.http.actions;

import java.util.Set;
import java.util.TreeSet;

import de.skuzzle.polly.core.internal.http.HttpInterface;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.http.AbstractAdminAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;
import de.skuzzle.polly.sdk.roles.RoleManager;



public class RoleHttpAction extends AbstractAdminAction {

    private RoleManager roleManager;
    
    
    public RoleHttpAction(MyPolly myPolly) {
        super("/Roles", myPolly);
        this.roleManager = myPolly.roles();
    }

    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException {
        HttpTemplateContext c = new HttpTemplateContext(HttpInterface.PAGE_ROLES);
        
        String action = e.getProperty("action");
        
        if (action != null && action.equals("addPermissionToRole")) {
            String role = e.getProperty("role");
            String permission = e.getProperty("permission");
            
            try {
                this.roleManager.assignPermission(role, permission);
            } catch (DatabaseException e1) {
                e.throwTemplateException(e1);
            } catch (RoleException e1) {
                e.throwTemplateException("Unknown permission", 
                        e1.getMessage());
            }
        } else if (action != null && action.equals("removePermissionFromRole")) {
            String role = e.getProperty("role");
            String permission = e.getProperty("permission");
            
            try {
                this.roleManager.removePermission(role, permission);
            } catch (RoleException e1) {
                e.throwTemplateException("Unknown role", 
                        e1.getMessage());
            } catch (DatabaseException e1) {
                e.throwTemplateException(e1);
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
