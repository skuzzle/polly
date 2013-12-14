package de.skuzzle.polly.core.internal.httpv2;

import java.util.List;
import java.util.Map;

import de.skuzzle.polly.http.annotations.Get;
import de.skuzzle.polly.http.annotations.OnRegister;
import de.skuzzle.polly.http.annotations.Param;
import de.skuzzle.polly.http.annotations.Post;
import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Atomic;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.ConstraintException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.InvalidUserNameException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;
import de.skuzzle.polly.sdk.exceptions.UserExistsException;
import de.skuzzle.polly.sdk.httpv2.GsonHttpAnswer;
import de.skuzzle.polly.sdk.httpv2.PollyController;
import de.skuzzle.polly.sdk.httpv2.SuccessResult;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.httpv2.html.HTMLTable;
import de.skuzzle.polly.sdk.roles.RoleManager;


public class UserController extends PollyController {
    
    
    private final static String ADMIN_CATEGORY_KEY = "userAdminCategory"; //$NON-NLS-1$
    private final static String USER_NAME_KEY = "userManagerPage"; //$NON-NLS-1$
    private final static String USER_DESC_KEY = "userManagerDesc"; //$NON-NLS-1$
    
    
    public final static String PAGE_USER_MANAGER = "/pages/users"; //$NON-NLS-1$
    public final static String PAGE_EDIT_USER = "/pages/editUser"; //$NON-NLS-1$
    
    private static final String CONTENT_USER_MANAGER = "templatesv2/users.html"; //$NON-NLS-1$
    private static final String CONTENT_EDIT_USER = "templatesv2/edit.user.html"; //$NON-NLS-1$
    
    public final static String API_DELETE_USER = "/api/deleteUser"; //$NON-NLS-1$
    public final static String API_SET_PASSWORD = "api/setPassword"; //$NON-NLS-1$
    public final static String API_ADD_USER = "/api/addUser"; //$NON-NLS-1$
    public final static String API_SET_ATTRIBUTE = "api/setAttribute"; //$NON-NLS-1$
    public final static String API_ADD_ROLE = "api/addRole"; //$NON-NLS-1$
    public final static String API_REMOVE_ROLE = "api/removeRole"; //$NON-NLS-1$
    
    
    
    public static void createUserTable(MyPolly myPolly) {
        final HTMLTable<User> table = new HTMLTable<>("userList",  //$NON-NLS-1$
            new UserTableModel(myPolly.users()), myPolly);
        
        myPolly.webInterface().getServer().addHttpEventHandler("/api/allUsers", table); //$NON-NLS-1$
    }

    
    
    public UserController(MyPolly myPolly) {
        super(myPolly);
    }

    
    
    @Override
    protected Controller createInstance() {
        return new UserController(this.getMyPolly());
    }
    
    
    
    @Get(value = PAGE_USER_MANAGER, name = USER_NAME_KEY)
    @OnRegister({
        WebinterfaceManager.ADD_MENU_ENTRY,
        MSG.FAMILY,
        ADMIN_CATEGORY_KEY,
        USER_DESC_KEY,
        RoleManager.ADMIN_PERMISSION
    })
    public HttpAnswer users() throws AlternativeAnswerException {
        this.requirePermissions(RoleManager.ADMIN_PERMISSION);
        final Map<String, Object> c = this.createContext(CONTENT_USER_MANAGER);
        c.put("users", this.getMyPolly().users().getRegisteredUsers()); //$NON-NLS-1$
        return this.makeAnswer(c);
    }
    
    
    
    @Get(PAGE_EDIT_USER)
    public HttpAnswer editUser(@Param("userId") int id) 
            throws AlternativeAnswerException {
        
        final User user = this.getMyPolly().users().getUser(id);
        
        if (user == null) {
            // TODO: 
        }
        
        if (this.getSessionUser() != user) {
            this.requirePermissions(RoleManager.ADMIN_PERMISSION);
        }
        
        final Map<String, Object> c = this.createContext(CONTENT_EDIT_USER);
        c.put("editUser", user); //$NON-NLS-1$
        return this.makeAnswer(c);
    }

    
    
    @Get(API_DELETE_USER)
    public HttpAnswer deleteUser(@Param("id") int id) throws AlternativeAnswerException {
        this.requirePermissions(RoleManager.ADMIN_PERMISSION);
        
        final UserManager um = this.getMyPolly().users();
        final User user = um.getUser(id);
     
        if (user == null) {
            return new GsonHttpAnswer(200, 
                new SuccessResult(false, MSG.bind(MSG.userUnknownId, id)));
        }
        
        try {
            um.deleteUser(user);
        } catch (UnknownUserException ignore) {
            // can not happen
            throw new RuntimeException(ignore);
        } catch (DatabaseException e) {
            return new GsonHttpAnswer(200, 
                new SuccessResult(false, MSG.userDatebaseFail));
        }
        return new GsonHttpAnswer(200, 
            new SuccessResult(true, MSG.bind(MSG.userDeleteSuccess, user.getName())));
    }
    
    
    
    @Post(API_SET_PASSWORD)
    public HttpAnswer setPassword(
        @Param("userId") int userId,
        @Param("newPassword") final String newPassword, 
        @Param("retype") String retype) throws DatabaseException, AlternativeAnswerException {
        
        final User user = this.getSessionUser();
        if (user == null || user.getId() != userId) {
            // session has no id OR session user is not the edited user:
            // then you need admin permissions to edit the attributes
            this.requirePermissions(RoleManager.ADMIN_PERMISSION);
        }
        
        final User target = this.getMyPolly().users().getUser(userId);
        if (target == null) {
            return new GsonHttpAnswer(200, 
                    new SuccessResult(false, MSG.bind(MSG.userUnknownId, userId)));
        } else if (!newPassword.equals(retype)) {
            return new GsonHttpAnswer(200, 
                new SuccessResult(false, MSG.userPasswordMismatch));
        } else {
            this.getMyPolly().persistence().writeAtomic(new Atomic() {
                
                @Override
                public void perform(Write write) {
                    target.setPassword(newPassword);
                }
            });
            return new GsonHttpAnswer(200, 
                    new SuccessResult(true, MSG.userPasswordChanged));
        }
    }
    
    
    
    
    @Post(API_ADD_USER)
    public HttpAnswer addUser(
        @Param("newName") String name, 
        @Param("newPassword") String password, 
        @Param(value = "initialRoles", typeHint = String.class) List<String> roles) 
                throws AlternativeAnswerException {
        
        this.requirePermissions(RoleManager.ADMIN_PERMISSION);
        
        final UserManager um = this.getMyPolly().users();
        final RoleManager rm = this.getMyPolly().roles();
            
        try {
            final User newUser = um.addUser(name, password);
            for (final String roleName : roles) {
                rm.assignRole(newUser, roleName);
            }
        } catch (InvalidUserNameException e) {
            return new GsonHttpAnswer(200, 
                new SuccessResult(false, MSG.bind(MSG.userInvalidFormat, name)));
        } catch (UserExistsException e) {
            return new GsonHttpAnswer(200, 
                new SuccessResult(false, MSG.bind(MSG.userAlreadyExists, name)));
        } catch (DatabaseException e) {
            return new GsonHttpAnswer(200, 
                new SuccessResult(false, MSG.userDatebaseFail));
        } catch (RoleException e) {
            return new GsonHttpAnswer(200, 
                new SuccessResult(false, e.getMessage()));
        }
        return new GsonHttpAnswer(200, 
            new SuccessResult(true, MSG.bind(MSG.userAdded, name)));
    }
    
    
    
    public final static class SetAttributeResult extends SuccessResult {
        public String newValue;

        public SetAttributeResult(boolean success, String msg, String newValue) {
            super(success, msg);
            this.newValue = newValue;
        }
    }
    
    
    
    @Get(API_SET_ATTRIBUTE)
    public HttpAnswer setAttribute(
        @Param("userId") int userId,
        @Param("attribute") String attribute, 
        @Param(value = "value", optional = true, defaultValue = "") String value) 
            throws AlternativeAnswerException {
        
        final User user = this.getSessionUser();
        final User target = this.getMyPolly().users().getUser(userId);
        if (user == null || user != target) {
            // session has no id OR session user is not the edited user:
            // then you need admin permissions to edit the attributes
            this.requirePermissions(RoleManager.ADMIN_PERMISSION);
        }
        
        if (target == null) {
            return new GsonHttpAnswer(200, 
                    new SuccessResult(false, MSG.bind(MSG.userUnknownId, userId)));
        }
        try {
            final String newValue = this.getMyPolly().users().setAttributeFor(
                user, target, attribute, value);
            
            return new GsonHttpAnswer(200, 
                new SetAttributeResult(true, "", newValue));  //$NON-NLS-1$
        } catch (DatabaseException e) {
            return new GsonHttpAnswer(200, 
                new SuccessResult(false, MSG.userDatebaseFail));
        } catch (ConstraintException e) {
            return new GsonHttpAnswer(200, 
                new SuccessResult(false, 
                        MSG.bind(MSG.userAttributeConstraintFail, value, attribute)));
        }
    }
    
    
    
    @Get(API_ADD_ROLE)
    public HttpAnswer addRole(
        @Param("userId") int userId, 
        @Param("role") String role) throws AlternativeAnswerException {

        this.requirePermissions(RoleManager.ADMIN_PERMISSION);
        
        final User target = this.getMyPolly().users().getUser(userId);
        
        if (target == null) {
            return new GsonHttpAnswer(200, 
                new SuccessResult(false, MSG.bind(MSG.userUnknownId, userId)));
        } else {
            try {
                this.getMyPolly().roles().assignRole(target, role);
                return new GsonHttpAnswer(200, new SuccessResult(true, MSG.userRoleAdded));
            } catch (RoleException e) {
                return new GsonHttpAnswer(200, 
                    new SuccessResult(false, e.getMessage()));
            } catch (DatabaseException e) {
                return new GsonHttpAnswer(200, new SuccessResult(false, 
                    MSG.userDatebaseFail));
            }
        }
    }
    
    
    
    @Get(API_REMOVE_ROLE)
    public HttpAnswer removeRole(
        @Param("userId") int userId, 
        @Param("role") String role) throws AlternativeAnswerException {

        this.requirePermissions(RoleManager.ADMIN_PERMISSION);
        final User target = this.getMyPolly().users().getUser(userId);
        
        if (target == null) {
            return new GsonHttpAnswer(200, 
                new SuccessResult(false, MSG.bind(MSG.userUnknownId, userId)));
        } else {
            try {
                if (target.isPollyAdmin() && role.equals(RoleManager.ADMIN_ROLE)) {
                    return new GsonHttpAnswer(200, new SuccessResult(false, 
                        MSG.userCantRemoveAdminRole));
                }
                this.getMyPolly().roles().removeRole(target, role);
                return new GsonHttpAnswer(200, new SuccessResult(true, 
                    MSG.userRoleRemoved));
            } catch (RoleException e) {
                return new GsonHttpAnswer(200, 
                    new SuccessResult(false, e.getMessage()));
            } catch (DatabaseException e) {
                return new GsonHttpAnswer(200, new SuccessResult(false, 
                    MSG.userDatebaseFail));
            }
        }
    }
}
