package de.skuzzle.polly.core.internal.roles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.skuzzle.polly.core.internal.users.UserImpl;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Atomic;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Param;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Read;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.roles.SecurityContainer;
import de.skuzzle.polly.sdk.roles.SecurityObject;


public class RoleManagerImpl implements RoleManager {
    
    private final static Logger logger = Logger.getLogger(RoleManagerImpl.class
        .getName());
    
    private final static Object SYNC = new Object();
    
    private PersistenceManagerV2 persistence;
    private boolean rolesStale;
    private Set<String> allRoles;
    
    
    
    public RoleManagerImpl(PersistenceManagerV2 persistence) {
        this.persistence = persistence;
    }
    
    

    @Override
    public boolean roleExists(String roleName) {
        synchronized (SYNC) {
            return this.getRoles().contains(roleName);
        }
    }
    
    

    @Override
    public boolean permissionExists(String permissionName) {
        return this.persistence.atomic().findSingle(Permission.class, 
            Permission.PERMISSION_BY_NAME, new Param(permissionName)) != null;
    }

    
    
    @Override
    public Set<String> getRoles() {
        synchronized(SYNC) {
            if (this.rolesStale || this.allRoles == null) {
                List<Role> roles = this.persistence.atomic().findList(
                    Role.class, Role.ALL_ROLES);
                this.allRoles = new HashSet<String>(roles.size());
                for (Role role : roles) {
                    this.allRoles.add(role.getName());
                }
                this.rolesStale = false;
            }
        }
        return Collections.unmodifiableSet(this.allRoles);
    }
    
    

    @Override
    public Set<String> getRoles(User user) {
        Set<String> result = new HashSet<String>();
        for (Role role : ((de.skuzzle.polly.core.internal.users.UserImpl)user).getRoles()) {
            result.add(role.getName());
        }
        return Collections.unmodifiableSet(result);
    }
    
    
    
    @Override
    public boolean hasRole(User user, String role) {
        return this.getRoles(user).contains(role);
    }
    
    
    
    @Override
    public Set<String> getPermissions(String roleName) {
        try (final Read r = this.persistence.read()) {
            Role role = r.findSingle(
                    Role.class, Role.ROLE_BY_NAME, new Param(roleName));
            
            if (role == null) {
                return Collections.emptySet();
            }
            return role.getPermissionNames();
        }
    }
    
    

    @Override
    public void createRole(final String newRoleName) 
                throws DatabaseException {
        synchronized(SYNC) {
            this.persistence.writeAtomic(new Atomic() {
                @Override
                public void perform(Write write) throws DatabaseException {
                    final Role role = write.read().findSingle(Role.class, 
                        Role.ROLE_BY_NAME, new Param(newRoleName));
                    
                    if (role != null) {
                        return;
                    }
                    
                    logger.info("Creating new Role: '" + newRoleName + "'");
                    write.single(new Role(newRoleName));
                }
            });
            this.rolesStale = true;
        }
    }
    
    

    @Override
    public void createRole(final String baseRoleName, final String newRoleName) 
                throws RoleException, DatabaseException {
        synchronized (SYNC) {
            try (final Write write = this.persistence.write()) {
                final Role role = write.read().findSingle(Role.class, 
                    Role.ROLE_BY_NAME, new Param(newRoleName));
                
                if (role != null) {
                    return;
                }
                
                final Role baseRole = write.read().findSingle(Role.class, 
                    Role.ROLE_BY_NAME, new Param(baseRoleName));
                
                if (baseRole == null) {
                    throw new RoleException("Unknown base role: '" + baseRoleName + "'");
                }
                logger.info("Creating new Role: '" + newRoleName + "' from base role '" + 
                    baseRoleName + "'");
                write.single(new Role(newRoleName, 
                    new HashSet<>(baseRole.getPermissions())));
            }
            this.rolesStale = true;
        }
    }
    
    
    
    @Override
    public void deleteRole(final String roleName) 
            throws RoleException, DatabaseException {
        if (roleName.equals(ADMIN_ROLE) || roleName.equals(DEFAULT_ROLE)) {
            throw new RoleException("Default roles cant be deleted");
        }
        this.persistence.writeAtomic(new Atomic() {
            @Override
            public void perform(Write write) throws DatabaseException {
                final Role role = write.read().findSingle(Role.class, Role.ROLE_BY_NAME, 
                    new Param(roleName));
                
                if (role == null) {
                    return;
                }
                
                final List<User> allUsers = write.read().findList(User.class, 
                    UserImpl.ALL_USERS);
                logger.debug("Deleting role: '" + roleName + "'");
                write.remove(role);
                
                for (User user : allUsers) {
                    UserImpl puser = (UserImpl) user;
                    puser.getRoles().remove(role);
                }
            }
        });
    }
    
    

    @Override
    public void registerPermission(final String permission) throws DatabaseException {
        synchronized(SYNC) {
            if (!permissionExists(permission)) {
                logger.debug("Registering permission: '" + permission + "'");
                this.persistence.writeAtomic(new Atomic() {
                    @Override
                    public void perform(Write write) throws DatabaseException {
                        write.single(new Permission(permission));
                    }
                });
            }
        }
    }

    
    
    @Override
    public void registerPermissions(final Set<String> permissions) 
                throws DatabaseException {
        synchronized(SYNC) {
            this.persistence.writeAtomic(new Atomic() {
                @Override
                public void perform(Write write) {
                    for (String perm : permissions) {
                        if (!permissionExists(perm)) {
                            logger.debug("Registering permission: '" + perm + "'");
                            write.single(new Permission(perm));
                        }
                    }
                }
            });
        }
    }
    
    
    
    @Override
    public void registerPermissions(SecurityContainer container)
            throws DatabaseException {
        this.registerPermissions(container.getContainedPermissions());
    }
    
    

    @Override
    public void assignPermission(final String roleName, final String permission) 
                throws DatabaseException, RoleException {
        
        synchronized(SYNC) {
            try (final Write write = this.persistence.write()) {
                final Role role = 
                    write.read().findSingle(Role.class, Role.ROLE_BY_NAME, 
                        new Param(roleName));
                
                if (role == null) {
                    throw new RoleException("Unknown role: " + roleName);
                }
                final Permission perm = write.read().findSingle(Permission.class, 
                    Permission.PERMISSION_BY_NAME, new Param(permission));
                
                if (perm == null) {
                    throw new RoleException("Unknown permission: " + permission);
                }
                // TODO: add permission to admin role
                logger.debug("Assigning permission '" + 
                    permission + "' to role '" + roleName + "'");
                
                role.getPermissions().add(perm);
                role.setStale(true); // this updates the permission name string set
            }
        }
    }
    
    
    
    @Override
    public void assignPermissions(String roleName, final Set<String> permissions) 
                throws RoleException, DatabaseException {
        
        synchronized(SYNC) {
            try (final Write write = this.persistence.write()) {
                final Role role = write.read().findSingle(Role.class, 
                    Role.ROLE_BY_NAME, new Param(roleName));
        
                if (role == null) {
                    throw new RoleException("Unknown role: " + roleName);
                }
            
                final List<Permission> perms = new ArrayList<Permission>(permissions.size());
                for (String permission : permissions) {
                    final Permission perm = write.read().findSingle(Permission.class, 
                        Permission.PERMISSION_BY_NAME, new Param(permission));
                    if (perm == null) {
                        throw new RoleException(
                            "Unknown permission: '" + permission + "'");
                    }
                    perms.add(perm);
                }
            
                // TODO: add permission to admin role
                logger.debug("Assigning permission '" + 
                    permissions + "' to role '" + roleName + "'");
                role.getPermissions().addAll(perms);
                role.setStale(true); // this updates the permission name string set
            }
        }
    }
    
    
    
    @Override
    public void assignPermissions(String roleName, SecurityContainer container) 
                throws RoleException, DatabaseException {
        this.assignPermissions(roleName, container.getContainedPermissions());
    }
    
    

    @Override
    public synchronized void removePermission(final String roleName, 
            final String permission) throws RoleException, DatabaseException {
        synchronized(SYNC) {
            try (final Write write = this.persistence.write()) {
                final Role role = write.read().findSingle(Role.class, Role.ROLE_BY_NAME, 
                    new Param(roleName));
                
                if (role == null) {
                    throw new RoleException("Unknown role: " + roleName);
                }
                
                final Permission perm = write.read().findSingle(Permission.class, 
                    Permission.PERMISSION_BY_NAME, new Param(permission));
                
                if (perm == null) {
                    return;
                }
                
                logger.debug("Removing permission '" + 
                    permission + "' from role '" + roleName + "'");
                role.getPermissions().remove(perm);
                role.setStale(true);
            }
        }
    }
    
    

    @Override
    public synchronized void assignRole(final User user, final String roleName) 
            throws RoleException, DatabaseException {
        synchronized (SYNC) {
            try (final Write w = this.persistence.write()) {
                final Role role = w.read().findSingle(Role.class, Role.ROLE_BY_NAME, 
                    new Param(roleName));
            
                if (role == null) {
                    throw new RoleException("Unknown role: " + roleName);
                }
                
                logger.debug("Assigning role '" + 
                    roleName + "' to user '" + user + "'");
                
                ((UserImpl) user).getRoles().add(role);
            }
        }
    }
    
    

    @Override
    public synchronized void removeRole(final User user, final String roleName) 
            throws RoleException, DatabaseException {
        
        synchronized (SYNC) {
            logger.debug("Removing role '" + 
                roleName + "' from user '" + user + "'");
            this.persistence.writeAtomic(new Atomic() {
                @Override
                public void perform(Write write) {
                    ((UserImpl) user).getRoles().remove(new Role(roleName));
                }
            });
        }
    }
    
    

    @Override
    public boolean hasPermission(User user, String permission) {
        if (permission.equals(RoleManager.NONE_PERMISSION)) {
            return true;
        } else if (user == null) {
            return false;
        }
        de.skuzzle.polly.core.internal.users.UserImpl puser = (de.skuzzle.polly.core.internal.users.UserImpl) user;
        
        synchronized (SYNC) {
            for (Role role : puser.getRoles()) {
                if (role.getName().equals(RoleManager.ADMIN_ROLE) || 
                        role.getPermissionNames().contains(permission)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    
    
    @Override
    public boolean hasPermission(User user, Set<String> permissions) {
        for (String perm : permissions) {
            if (!this.hasPermission(user, perm)) {
                return false;
            }
        }
        return true;
    }

    
    
    
    @Override
    public boolean canAccess(User user, SecurityObject securityObject) {
        return this.hasPermission(user, securityObject.getRequiredPermission());
    }
    
    
    
    @Override
    public void checkAccess(User user, SecurityObject securityObject)
            throws InsufficientRightsException {
        if (!this.canAccess(user, securityObject)) {
            this.denyAccess(securityObject);
        }
    }
    
    
    
    @Override
    public void denyAccess(SecurityObject securityObject) 
            throws InsufficientRightsException {
        throw new InsufficientRightsException(securityObject);
    }
}
