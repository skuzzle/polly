package de.skuzzle.polly.core.internal.httpv2;

import java.util.Map;

import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.Get;
import de.skuzzle.polly.http.api.Param;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.ConstraintException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.httpv2.HttpManagerV2;
import de.skuzzle.polly.sdk.roles.RoleManager;


public class UserController extends PollyController {

    public UserController(MyPolly myPolly, HttpManagerV2 httpManager) {
        super(myPolly, httpManager);
    }

    
    
    @Override
    protected Controller createInstance() {
        return new UserController(this.getMyPolly(), this.getHttpManager());
    }
    
    
    
    @Get("content/users")
    public HttpAnswer users() {
        final Map<String, Object> c = this.createContext();
        c.put("users", this.getMyPolly().users().getRegisteredUsers());
        return HttpAnswers.createTemplateAnswer("templates/users/users.html", c);
    }
    
    
    
    @Get("content/addUserInput")
    public HttpAnswer addUserInput() {
        return HttpAnswers.createTemplateAnswer("templates/users/addUserInput.html", 
            this.createContext());
    }
    
    
    
    @Get("content/editUser")
    public HttpAnswer editUser(@Param("name") String name) {
        final User u = this.getMyPolly().users().getUser(name);
        final Map<String, Object> c = this.createContext();
        c.put("editUser", u);
        return HttpAnswers.createTemplateAnswer("templates/users/showUser.html", c);
    }

    
    
    @Get("/api/listUsers")
    public HttpAnswer listUsers() {
        final Map<String, Object> c = this.createContext();
        c.put("users", this.getMyPolly().users().getRegisteredUsers());
        return HttpAnswers.createTemplateAnswer("templates/users/listUsers.html", c);
    }
    
    
    
    @Get("api/getUser")
    public HttpAnswer getUser(@Param("name") String name) {
        final User u = this.getMyPolly().users().getUser(name);
        return new GsonHttpAnswer(200, u);
    }
    
    
    
    
    public final static class SetAttributeResult {
        
    }
    
    
    
    @Get("api/setAttribute")
    public HttpAnswer setAttribute(
        @Param("user") String user,
        @Param("attribute") String attribute, 
        @Param("value") String value) throws InsufficientRightsException {
        
        this.requirePermissions(RoleManager.ADMIN_PERMISSION);
        
        final User u = this.getMyPolly().users().getUser(user);
        if (u == null) {
            return HttpAnswers.createStringAnswer("fail");
        }
        
        try {
            this.getMyPolly().users().setAttributeFor(u, attribute, value);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpAnswers.createStringAnswer("fail");
        }
        return HttpAnswers.createStringAnswer("success");
    }
}
