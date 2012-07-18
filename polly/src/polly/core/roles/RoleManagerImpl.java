package polly.core.roles;

import java.util.Set;

import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.WriteAction;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.model.User;
import de.skuzzle.polly.sdk.roles.RoleManager;


public class RoleManagerImpl implements RoleManager {
    
    private PersistenceManager persistence;
    private UserManager userManager;
    private boolean rolesStale;
    private Set<Role> allRoles;
    
    
    
    public RoleManagerImpl(PersistenceManager persistence) {
        this.persistence = persistence;
    }
    
    

    @Override
    public boolean roleExists(String roleName) {
        return this.persistence.findSingle(Role.class, 
            Role.ROLE_BY_NAME, roleName) != null;
    }
    
    

    @Override
    public boolean permissionExists(String permissionName) {
        return this.persistence.findSingle(Permission.class, 
            Permission.PERMISSION_BY_NAME, permissionName) != null;
    }

    
    
    @Override
    public Set<String> getRoles() {
        if (this.rolesStale || this.allRoles == null) {
            //this.allRoles = this.persistence.a
        }
    }
    
    

    @Override
    public Set<String> getRoles(User user) {

    }
    
    

    @Override
    public void createRole(String newRoleName) {
    }
    
    

    @Override
    public void createRole(String baseRoleName, String newRoleName) {
    }
    
    

    @Override
    public void registerPermission(final String permission) throws RoleException {
        try {
            this.persistence.atomicWriteOperation(new WriteAction() {
                
                @Override
                public void performUpdate(PersistenceManager persistence) {
                    if (!permissionExists(permission)) {
                        persistence.persist(new Permission(permission));
                    }
                }
            });
        } catch (DatabaseException e) {
            throw new RoleException(e);
        }
    }

    
    
    @Override
    public void registerPermissions(final Set<String> permissions) throws RoleException {
        try {
            this.persistence.atomicWriteOperation(new WriteAction() {
                
                @Override
                public void performUpdate(PersistenceManager persistence) {
                    for (String perm : permissions) {
                        if (!permissionExists(perm)) {
                            persistence.persist(new Permission(perm));
                        }
                    }
                }
            });
        } catch (DatabaseException e) {
            throw new RoleException(e);
        }
    }
    
    

    @Override
    public void addPermission(final String roleName, final String permission) 
                throws RoleException {
        final Role role = 
            this.persistence.findSingle(Role.class, Role.ROLE_BY_NAME, roleName);
    
        if (role == null) {
            throw new RoleException("Unknown role: " + roleName);
        }
        
        final Permission perm = this.persistence.findSingle(Permission.class, 
                Permission.PERMISSION_BY_NAME, permission);
        
        if (perm == null) {
            throw new RoleException("Unknown permission: " + roleName);
        }
        
        try {
            this.persistence.atomicWriteOperation(new WriteAction() {
                
                @Override
                public void performUpdate(PersistenceManager persistence) {
                    role.getPermissions().add(perm);
                }
            });
        } catch (DatabaseException e) {
            throw new RoleException(e);
        }
    }
    
    

    @Override
    public synchronized void removePermission(final String roleName, 
            final String permission) throws RoleException {
        final Role role = 
                this.persistence.findSingle(Role.class, Role.ROLE_BY_NAME, roleName);
        
        if (role == null) {
            throw new RoleException("Unknown role: " + roleName);
        }
        
        try {
            this.persistence.atomicWriteOperation(new WriteAction() {
                
                @Override
                public void performUpdate(PersistenceManager persistence) {
                    role.getPermissions().remove(new Permission(permission));
                }
            });
        } catch (DatabaseException e) {
            throw new RoleException(e);
        }
    }
    
    

    @Override
    public synchronized void assignRole(final User user, final String roleName) 
            throws RoleException {
        final Role role = 
            this.persistence.findSingle(Role.class, Role.ROLE_BY_NAME, roleName);
    
        if (role == null) {
            throw new RoleException("Unknown role: " + roleName);
        }
        
        try {
            this.persistence.atomicWriteOperation(new WriteAction() {
                
                @Override
                public void performUpdate(PersistenceManager persistence) {
                    ((polly.core.users.User)user).getRoles().add(role);
                }
            });
        } catch (DatabaseException e) {
            throw new RoleException(e);
        }
    }
    
    

    @Override
    public synchronized void removeRole(final User user, final String roleName) 
            throws RoleException {
        try {
            this.persistence.atomicWriteOperation(new WriteAction() {
                
                @Override
                public void performUpdate(PersistenceManager persistence) {
                    ((polly.core.users.User)user).getRoles().remove(new Role(roleName));
                }
            });
        } catch (DatabaseException e) {
            throw new RoleException(e);
        }
    }
    
    

    @Override
    public boolean hasPermission(User user, String permission) {
        polly.core.users.User puser = (polly.core.users.User) user;
        
        for (Role role : puser.getRoles()) {
            if (role.getPermissions().contains(permission)) {
                return true;
            }
        }
        return false;
    }
    
    

    @Override
    public Set<String> getPermissions(String roleName) {
        return null;
    }
    
    
    
    private boolean checkNameConvention(String roleOrPermission, String roleName) {
        String compare = "polly." + roleOrPermission + ".";
        if (!roleName.startsWith(compare)) {
            return false;
        } else {
            String sub = roleName.substring(compare.length());
            return sub.equals(sub.toUpperCase());
        }
    }

}
