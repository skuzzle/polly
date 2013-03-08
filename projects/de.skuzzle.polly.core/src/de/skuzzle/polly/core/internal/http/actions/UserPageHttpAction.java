package de.skuzzle.polly.core.internal.http.actions;

import java.util.List;


import de.skuzzle.polly.core.internal.http.HttpInterface;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.InvalidUserNameException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;
import de.skuzzle.polly.sdk.exceptions.UserExistsException;
import de.skuzzle.polly.sdk.http.AbstractAdminAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;
import de.skuzzle.polly.sdk.model.User;



public class UserPageHttpAction extends AbstractAdminAction {

    
    public UserPageHttpAction(MyPolly myPolly) {
        super("/Users", myPolly);
    }
    
    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException {
        HttpTemplateContext context = new HttpTemplateContext(HttpInterface.PAGE_USERS);
        context.put("formatter", this.myPolly.formatting());
        String action = e.getProperty("action");
        
        if (action != null && action.equals("delete")) {
            String userName = e.getProperty("userName");
            User user = this.myPolly.users().getUser(userName);
            
            if (user != null && !user.equals(this.myPolly.users().getAdmin())) {
                try {
                    this.myPolly.users().deleteUser(user);
                } catch (UnknownUserException e1) {
                    e.throwTemplateException("Unknown user",
                            "User with name " + userName + " does not exist");
                } catch (DatabaseException e1) {
                    e.throwTemplateException(e1);
                }
            } else {
                e.throwTemplateException("User can not be deleted", 
                    "This user is protected and can not be deleted!");
            }
            
        } else if (action != null && action.equals("addUser")) {
            String newName = e.getProperty("newname");
            String password = e.getProperty("password");
            String[] roles = e.getProperty("roles") != null
                    ? e.getProperty("roles").split(";") 
                    : new String[0];
            
            if (password == null || password.equals("")) {
                e.throwTemplateException("Invalid password", 
                        "Password must not be empty");
            } else if (newName == null || newName.equals("")) {
                e.throwTemplateException("Invalid user name", 
                        "User name must not be empty");
            }
            
            UserManager userManager = this.myPolly.users();
            
            try {
                User newUser = userManager.addUser(newName, password);
                for (String role : roles) {
                    this.myPolly.roles().assignRole(newUser, role);
                }
            } catch (InvalidUserNameException e1) {
                e.throwTemplateException("Can not add user", 
                    "Invalid user name: " + newName);
            } catch (UserExistsException e1) {
                e.throwTemplateException("Can not add user", 
                        "User with that name already exists");
            } catch (DatabaseException e1) {
                e.throwTemplateException(e1);
            } catch (RoleException e1) {
                e.throwTemplateException("Unknown role", 
                        e1.getMessage());
            }
        }
        
        List<User> users = this.myPolly.users().getRegisteredUsers();
        context.put("users", users);
        
        return context;
    }
}
