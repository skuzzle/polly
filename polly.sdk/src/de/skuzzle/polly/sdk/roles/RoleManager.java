package de.skuzzle.polly.sdk.roles;

import java.util.Set;

import de.skuzzle.polly.sdk.FormalSignature;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.model.User;



/**
 * <p>This class manages roles in polly. By now, roles are only important when executing 
 * commands. If you create your own Commands, each {@link FormalSignature} of your 
 * command can be assigned a required permission. If a user wants to execute that 
 * signature, the command first checks if the executing user has a role assigned which 
 * contains that particular permission. If not, he won't be able to execute the command
 * and will get an {@link InsufficientRightsException}.</p>
 * 
 * <p>When creating your commands you have to obey the role- and permission naming 
 * convention. A role name always starts with <code>polly.role.</code> followed by your
 * desired name in upper case. Permission names must always start with 
 * <code>polly.permission.</code> followed by the desired name in upper case. Role- and
 * permission names must be canonical.</p>
 * 
 * @author Simon
 * @since 0.9.1
 */
public interface RoleManager {
    /**
     * Constant for a permission name for which {@link #hasPermission(User, String)}
     * will always return <code>true</code>
     */
    public final static String NONE_PERMISSIONS = "polly.permissions.NONE";
    
    /**
     * Constant for a permission that all registered users own.
     */
    public final static String REGISTERED_PERMISSION = "polly.permissions.REGISTERED";
    
    /**
     * Constant for the admin role name. This role has permissions for accessing every 
     * {@link SecurityObject} within polly.
     */
    public final static String ADMIN_ROLE = "polly.roles.ADMIN";
    
    /**
     * Constant for the default role name. This role will be assigned to all registered 
     * users.
     */
    public final static String DEFAULT_ROLE = "polly.roles.DEFAULT";
    
    
    public abstract boolean roleExists(String roleName);
    
    public abstract boolean permissionExists(String permissionName);
    
    public abstract Set<String> getRoles();
    
    public abstract Set<String> getRoles(User user);
    
    public abstract void createRole(String newRoleName) throws DatabaseException, RoleException;
    
    public abstract void createRole(String baseRoleName, String newRoleName) throws RoleException, DatabaseException;
    
    public abstract void registerPermission(String permission) throws DatabaseException;
    
    public abstract void registerPermissions(Set<String> permissions) throws DatabaseException;

    public abstract void addPermission(String roleName, String permission) throws DatabaseException, RoleException;
    
    public abstract void removePermission(String roleName, String permission) throws RoleException, DatabaseException;
    
    public abstract void assignRole(User user, String roleName) throws RoleException, DatabaseException;
    
    public abstract void removeRole(User user, String roleName) throws RoleException, DatabaseException;
    
    public abstract boolean hasPermission(User user, String permission);
    
    public abstract boolean hasPermission(User user, Set<String> permissions);
    
    public abstract Set<String> getPermissions(String roleName);
}
