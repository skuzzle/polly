package de.skuzzle.polly.core.internal.httpv2;

import java.util.List;
import java.util.Map;

import de.skuzzle.polly.http.annotations.Get;
import de.skuzzle.polly.http.annotations.OnRegister;
import de.skuzzle.polly.http.annotations.Param;
import de.skuzzle.polly.http.annotations.Post;
import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.LazyResolvedFile;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.InvalidUserNameException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;
import de.skuzzle.polly.sdk.exceptions.UserExistsException;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.roles.RoleManager;


public class UserController extends PollyController {

    public UserController(MyPolly myPolly, String rootDir, 
            WebinterfaceManager httpManager) {
        super(myPolly, rootDir, httpManager);
    }

    
    
    @Override
    protected Controller createInstance() {
        return new UserController(this.getMyPolly(), 
            this.getRootDir(), this.getHttpManager());
    }
    
    
    
    @Get(value = "pages/users", name = "User Manager")
    @OnRegister({
        WebinterfaceManager.ADD_MENU_ENTRY,
        "Admin",
        "List, modify, add and delete polly user accounts.",
        RoleManager.ADMIN_PERMISSION
    })
    public HttpAnswer users() throws AlternativeAnswerException {
        this.requirePermissions(RoleManager.ADMIN_PERMISSION);
        final Map<String, Object> c = this.createContext("templatesv2/users.html");
        c.put("users", this.getMyPolly().users().getRegisteredUsers());
        return this.makeAnswer(c);
    }

    
    
    public final class DeleteUserResult {
        public final boolean success;
        public final String message;
        public DeleteUserResult(boolean success, String message) {
            super();
            this.success = success;
            this.message = message;
        }
    }
    
    @Get("/api/deleteUser")
    public HttpAnswer deleteUser(@Param("id") int id) {
        final UserManager um = this.getMyPolly().users();
        final User user = um.getUser(id);
     
        if (user == null) {
            return new GsonHttpAnswer(200, 
                new DeleteUserResult(false, "No user with id '" + id +"'"));
        }
        
        try {
            um.deleteUser(user);
        } catch (UnknownUserException e) {
            return new GsonHttpAnswer(200, 
                new DeleteUserResult(false, "Unknown user"));
        } catch (DatabaseException e) {
            return new GsonHttpAnswer(200, 
                new DeleteUserResult(false, "Database exception while deleting user"));
        }
        return new GsonHttpAnswer(200, 
            new DeleteUserResult(true, user.getName() + " has been deleted"));
    }
    
    
    
    @Get("/api/listUsers")
    public HttpAnswer listUsers() {
        if (!this.getMyPolly().roles().hasPermission(
            this.getSessionUser(), RoleManager.ADMIN_PERMISSION)) {
        
            return HttpAnswers.newStringAnswer("Required permission: " + 
                RoleManager.ADMIN_PERMISSION);
        }
        final Map<String, Object> c = this.createContext("");
        c.put("users", this.getMyPolly().users().getRegisteredUsers());
        return HttpAnswers.newTemplateAnswer(
            new LazyResolvedFile(this.getRootDir(), "templatesv2/users.list.html"), c);
    }
    
    
    
    @Get("api/getUser")
    public HttpAnswer getUser(@Param("name") String name) {
        final User u = this.getMyPolly().users().getUser(name);
        return new GsonHttpAnswer(200, u);
    }
    
    
    
    public class AddUserResult {
        public boolean success;
        public final String message;
        public AddUserResult(boolean success, String message) {
            super();
            this.success = success;
            this.message = message;
        }
    }
    
    
    @Post("/api/addUser")
    public HttpAnswer addUser(
        @Param("newName") String name, 
        @Param("newPassword") String password, 
        @Param(value = "initialRoles", typeHint = String.class) List<String> roles) {
        
        final UserManager um = this.getMyPolly().users();
        final RoleManager rm = this.getMyPolly().roles();
            
        try {
            final User newUser = um.addUser(name, password);
            for (final String roleName : roles) {
                rm.assignRole(newUser, roleName);
            }
        } catch (InvalidUserNameException e) {
            return new GsonHttpAnswer(200, new AddUserResult(false, "Username has invalid format"));
        } catch (UserExistsException e) {
            return new GsonHttpAnswer(200, new AddUserResult(false, "Username already exists"));
        } catch (DatabaseException e) {
            return new GsonHttpAnswer(200, new AddUserResult(false, "Database error while storing new user"));
        } catch (RoleException e) {
            return new GsonHttpAnswer(200, new AddUserResult(false, "Error while assigning initial roles"));
        }
        return new GsonHttpAnswer(200, new AddUserResult(true, "User '" + name + "' added."));
    }
    
    
    public final static class SetAttributeResult {
        
    }
    
    
    
    @Get("api/setAttribute")
    public HttpAnswer setAttribute(
        @Param("user") String user,
        @Param("attribute") String attribute, 
        @Param("value") String value) throws AlternativeAnswerException {
        
        this.requirePermissions(RoleManager.ADMIN_PERMISSION);
        
        final User u = this.getMyPolly().users().getUser(user);
        if (u == null) {
            return HttpAnswers.newStringAnswer("fail");
        }
        
        try {
            this.getMyPolly().users().setAttributeFor(u, attribute, value);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpAnswers.newStringAnswer("fail");
        }
        return HttpAnswers.newStringAnswer("success");
    }
}
