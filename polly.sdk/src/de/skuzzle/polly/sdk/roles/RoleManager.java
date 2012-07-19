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
 * permission names must be unique.</p>
 * 
 * @author Simon
 * @since 0.9.1
 */
public interface RoleManager {
    /**
     * Constant for a permission name for which {@link #hasPermission(User, String)}
     * will always return <code>true</code>
     */
    public final static String NONE_PERMISSION = "polly.permissions.NONE";
    
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
    
    
    
    /**
     * Checks whether a role with the given name exists.
     * 
     * @param roleName The role name to check.
     * @return <code>true</code> iff that role exists.
     */
    public abstract boolean roleExists(String roleName);
    
    
    
    /**
     * Checks whether a permission with the given name exists.
     * 
     * @param permissionName The permission name to check.
     * @return <code>true</code> iff that permission exists.
     */
    public abstract boolean permissionExists(String permissionName);
    
    
    
    /**
     * Gets a readonly view of all existing roles.
     * 
     * @return An unmodifiable set of all existing role names.
     */
    public abstract Set<String> getRoles();
    
    
    
    /**
     * Gets a readonly view of all roles that an existing user owns.
     * 
     * @param user The user which roles should be retrieved.
     * @return An unmodifiable set of all existing role names.
     */
    public abstract Set<String> getRoles(User user);
    
    
    
    /**
     * <p>Creates a new role with the given name. Role names must be unique. If you
     * try to create a role with a name that already exists, this method simply
     * does nothing.</p>
     * 
     * <p>Role names must follow a naming convention. They must start with "polly.role."
     * followed by the role name in uppercase. You may specify further dots in order to 
     * group roles such like "polly.role.myplugin.MODIFY_DATA".</p>
     * 
     * @param newRoleName The full qualified name of the new role.
     * @throws DatabaseException If persisting the new rule fails.
     */
    public abstract void createRole(String newRoleName) 
            throws DatabaseException;
    
    
    
    /**
     * <p>Creates a new role with the given name. Role names must be unique. If you
     * try to create a role with a name that already exists, this method simply
     * does nothing. Additionally, this method copies all permissions of a base role to 
     * the new role.</p>
     * 
     * <p>Role names must follow a naming convention. They must start with "polly.role."
     * followed by the role name in uppercase. You may specify further dots in order to 
     * group roles such like "polly.role.myplugin.MODIFY_DATA".</p>
     * 
     * @param baseRoleName The name of the base role which permissions will be copied to
     *          the new role.
     * @param newRoleName The full qualified name of the new role.
     * @throws DatabaseException If persisting the new rule fails.
     * @throws RoleException If the base role does not exist.
     */
    public abstract void createRole(String baseRoleName, String newRoleName) 
            throws RoleException, DatabaseException;
    
    
    
    /**
     * <p>Registers a new permission. That means that the permission is made known to 
     * polly. Every permission must be registered before it can be assigned to any 
     * role using {@link #assignPermission(String, String)}.</p>
     * 
     * <p>Permissions must be unique. If a permission with the same name already exists,
     * this method will simply do nothing.</p>
     * 
     * <p>Permission names must follow a naming convention. They must start with 
     * "polly.permission." followed by the permission name in uppercase. You may specify 
     * further dots in order to permissions roles such like 
     * "polly.role.myplugin.MODIFY_DATA".</p>
     * 
     * @param permission The permission to register.
     * @throws DatabaseException If persisting the new permission fails.
     */
    public abstract void registerPermission(String permission) throws DatabaseException;
    
    
    
    /**
     * <p>Registers a whole set of permissions to make them known to polly. Permissions
     * must be unique. If the set contains any permission names that already exist, they
     * will simply be skipped.</p>
     * 
     * <p>Permission names must follow a naming convention. They must start with 
     * "polly.permission." followed by the permission name in uppercase. You may specify 
     * further dots in order to permissions roles such like 
     * "polly.role.myplugin.MODIFY_DATA".</p>
     * 
     * @param permissions A set of permissions to be registered.
     * @throws DatabaseException If persisting the new permissions fails.
     */
    public abstract void registerPermissions(Set<String> permissions) 
            throws DatabaseException;
    
    
    
    /**
     * Registers all the permissions of the given {@link SecurityContainer}.
     * 
     * @param container The container which permissions should be registered.
     * @throws DatabaseException If persisting the new permissions fails.
     * @see #registerPermission(String)
     * @see #registerPermissions(Set)
     */
    public abstract void registerPermissions(SecurityContainer container) 
            throws DatabaseException;

    
    
    /**
     * <p>Assigns a permission to a role. If the role already contains that permission, 
     * nothing happens. If the role or the permission does not already exist, a 
     * {@link RoleException} is thrown. Thus each role and permission must be made 
     * known to polly using any of the "register" e.g "create methods of this class.</p>
     * 
     * <p>After assigning a permission to the role, each user that owns that role can 
     * access {@link SecurityObject}s that require that permission.</p>
     * 
     * @param roleName The role to which the permission should be added.
     * @param permission The permission to add to the role.
     * @throws DatabaseException If assigning the permission to the role fails.
     * @throws RoleException If no permission or role with the given names exists.
     * @see #registerPermission(String)
     * @see #registerPermissions(Set)
     * @see #createRole(String)
     * @see #createRole(String, String)
     */
    public abstract void assignPermission(String roleName, String permission) 
            throws DatabaseException, RoleException;
    
    
    
    /**
     * Assigns all permissions from the given set to the given role. If the role or any
     * of the permissions from the set does not exist, a RoleException is thrown.
     * 
     * @param roleName The role to which the permissions should be added.
     * @param permissions The set of permissions to add to the role.
     * @throws RoleException If the role or any of the permissions does not exist.
     * @throws DatabaseException If assigning the permission to the role fails.
     * @see #assignPermission(String, String)
     */
    public abstract void assignPermissions(String roleName, Set<String> permissions) 
            throws RoleException, DatabaseException;
    
    
    
    /**
     * Assigns all permissions exported by the given {@link SecurityContainer} to the 
     * given role. If the role or any of the contained permissions does not exist, a 
     * RoleException is thrown.
     * 
     * @param roleName The role to which the permissions should be added.
     * @param container The SecurityContainer that exports a set of permissions.
     * @throws RoleException If the role or any of the permissions does not exist.
     * @throws DatabaseException If assigning the permission to the role fails.
     * @see #assignPermissions(String, Set)
     * @see #assignPermission(String, String)
     */
    public abstract void assignPermissions(String roleName, SecurityContainer container) 
            throws RoleException, DatabaseException;
    
    
    
    /**
     * Removes a permission from a role. If the role does not exist, a 
     * {@link RoleException} is thrown. If that permission does not belong to the given 
     * role, nothing happens.
     * 
     * @param roleName The role name from which the permission should be removed.
     * @param permission The permission to remove.
     * @throws RoleException If the role does not exist.
     * @throws DatabaseException If removing the permission fails.
     */
    public abstract void removePermission(String roleName, String permission) 
            throws RoleException, DatabaseException;
    
    
    
    
    /**
     * <p>Assigns a role to an user. This method will throw a RoleException if no role 
     * with the given name exists. If the user already owns the role, nothing happens.</p>
     * 
     * <p>After assigning a role, the user is allowed to access {@link SecurityObject}s 
     * that require any of the permissions that are contained in that role.</p>
     * 
     * @param user The user to assign the role to.
     * @param roleName The name of the role to assign.
     * @throws RoleException If a role with that name does not exist.
     * @throws DatabaseException If assigning the role fails.
     */
    public abstract void assignRole(User user, String roleName) 
            throws RoleException, DatabaseException;
    
    
    
    /**
     * Removes a role from a user. This is the exact counterpart of the method 
     * {@link #assignRole(User, String)}.
     * 
     * @param user The user to remove the role from.
     * @param roleName The role to remove.
     * @throws RoleException If no rule with the given name exist.
     * @throws DatabaseException If removing the role fails.
     */
    public abstract void removeRole(User user, String roleName) 
            throws RoleException, DatabaseException;
    
    
    
    /**
     * Checks whether a user has the given permission. 
     * 
     * @param user The user to check.
     * @param permission The permission to check.
     * @return <code>true</code> if the user owns a role that contains the given 
     *          permission.
     */
    public abstract boolean hasPermission(User user, String permission);
    
    
    
    /**
     * Checks whether a user has all the given permissions.
     * 
     * @param user The user to check.
     * @param permissions A set of permissions that the user should have.
     * @return <code>true</code> if the user owns all the given permissions.
     */
    public abstract boolean hasPermission(User user, Set<String> permissions);
    
    
    
    /**
     * Checks whether the given user has all the permissions required to access the given
     * {@link SecurityObject}.
     * 
     * @param user The user to check.
     * @param securityObject The SecurityObject that should be accessed.
     * @return <code>true</code> if the user has all required permissions.
     */
    public abstract boolean canAccess(User user, SecurityObject securityObject);
    
    
    
    /**
     * Gets a read only view of all permissions belonging to the given role name.
     * 
     * @param roleName The nam of the role which permissions shall be retrieved.
     * @return An unmodifiable set of permission names belonging to that role.
     */
    public abstract Set<String> getPermissions(String roleName);
}
