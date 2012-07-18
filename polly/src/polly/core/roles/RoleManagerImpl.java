package polly.core.roles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.WriteAction;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.model.User;
import de.skuzzle.polly.sdk.roles.RoleManager;


public class RoleManagerImpl implements RoleManager {
    
    private final static Object SYNC = new Object();
    
    private PersistenceManager persistence;
    private boolean rolesStale;
    private Set<String> allRoles;
    
    
    
    public RoleManagerImpl(PersistenceManager persistence) {
        this.persistence = persistence;
    }
    
    

    @Override
    public boolean roleExists(String roleName) {
        try {
            this.persistence.readLock();
            return this.persistence.findSingle(Role.class, 
                Role.ROLE_BY_NAME, roleName) != null;
        } finally {
            this.persistence.readUnlock();
        }
    }
    
    

    @Override
    public boolean permissionExists(String permissionName) {
        try {
            this.persistence.readLock();
            return this.persistence.findSingle(Permission.class, 
                Permission.PERMISSION_BY_NAME, permissionName) != null;
        } finally {
            this.persistence.readUnlock();
        }
    }

    
    
    @Override
    public Set<String> getRoles() {
        synchronized(SYNC) {
            if (this.rolesStale || this.allRoles == null) {
                List<Role> roles = this.persistence.atomicRetrieveList(
                    Role.class, Role.ALL_ROLES);
                this.allRoles = new HashSet<String>(roles.size());
                for (Role role : roles) {
                    this.allRoles.add(role.getName());
                }
                this.rolesStale = false;
            }
        }
        return this.allRoles;
    }
    
    

    @Override
    public Set<String> getRoles(User user) {
        Set<String> result = new HashSet<String>();
        for (Role role : ((polly.core.users.User)user).getRoles()) {
            result.add(role.getName());
        }
        return result;
    }
    
    
    
    @Override
    public Set<String> getPermissions(String roleName) {
        try {
            this.persistence.readLock();
            Role role = this.persistence.findSingle(
                    Role.class, Role.ROLE_BY_NAME, roleName);
            
            return role.getPermissionNames();
        } finally {
            this.persistence.readUnlock();
        }
    }
    
    

    @Override
    public void createRole(final String newRoleName) 
                throws DatabaseException, RoleException {
        synchronized(SYNC) {
            try {
                this.persistence.writeLock();
                    final Role role = 
                        this.persistence.findSingle(
                            Role.class, Role.ROLE_BY_NAME, newRoleName);
                    
                    if (role != null) {
                        throw new RoleException(
                            "Role already exists: '" + newRoleName + "'");
                    }
                    
                    this.persistence.startTransaction();
                    this.persistence.persist(new Role(newRoleName));
                    this.persistence.commitTransaction();
                    this.rolesStale = true;
            } finally {
                this.persistence.writeUnlock();
            }
        }
    }
    
    

    @Override
    public void createRole(String baseRoleName, String newRoleName) 
                throws RoleException, DatabaseException {
        synchronized (SYNC) {
            try {
                this.persistence.writeLock();
                final Role role = 
                    this.persistence.findSingle
                    (Role.class, Role.ROLE_BY_NAME, newRoleName);
                
                if (role != null) {
                    throw new RoleException(
                        "Role already exists: '" + newRoleName + "'");
                }
                
                Role baseRole = 
                    this.persistence.findSingle(
                        Role.class, Role.ROLE_BY_NAME, baseRoleName);
                
                if (baseRole == null) {
                    throw new RoleException("Unknown base role: '" + baseRoleName + "'");
                }
                
                this.persistence.startTransaction();
                this.persistence.persist(new Role(newRoleName, 
                        new HashSet<Permission>(baseRole.getPermissions())));
                this.persistence.commitTransaction();
                this.rolesStale = true;
            } finally {
                this.persistence.writeUnlock();
            }
        }
    }
    
    

    @Override
    public void registerPermission(final String permission) throws DatabaseException {
        synchronized(SYNC) {
            this.persistence.atomicWriteOperation(new WriteAction() {
                
                @Override
                public void performUpdate(PersistenceManager persistence) {
                    if (!permissionExists(permission)) {
                        persistence.persist(new Permission(permission));
                    }
                }
            });
        }
    }

    
    
    @Override
    public void registerPermissions(final Set<String> permissions) 
                throws DatabaseException {
        synchronized(SYNC) {
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
        }
    }
    
    

    @Override
    public void addPermission(final String roleName, final String permission) 
                throws DatabaseException, RoleException {
        
        synchronized(SYNC) {
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
            
            // TODO: add permission to admin role
            
            this.persistence.atomicWriteOperation(new WriteAction() {
                
                @Override
                public void performUpdate(PersistenceManager persistence) {
                    role.getPermissions().add(perm);
                    role.setStale(true); // this updates the permission name string set
                }
            });
        }
    }
    
    

    @Override
    public synchronized void removePermission(final String roleName, 
            final String permission) throws RoleException, DatabaseException {
        synchronized(SYNC) {
            final Role role = 
                    this.persistence.findSingle(Role.class, Role.ROLE_BY_NAME, roleName);
            
            if (role == null) {
                throw new RoleException("Unknown role: " + roleName);
            }
            
            // TODO: remove permission from admin role
            
            this.persistence.atomicWriteOperation(new WriteAction() {
                
                @Override
                public void performUpdate(PersistenceManager persistence) {
                    role.getPermissions().remove(new Permission(permission));
                    role.setStale(true);
                }
            });
        }
    }
    
    

    @Override
    public synchronized void assignRole(final User user, final String roleName) 
            throws RoleException, DatabaseException {
        synchronized (SYNC) {
            final Role role = 
                this.persistence.findSingle(Role.class, Role.ROLE_BY_NAME, roleName);
        
            if (role == null) {
                throw new RoleException("Unknown role: " + roleName);
            }
            
            this.persistence.atomicWriteOperation(new WriteAction() {
                @Override
                public void performUpdate(PersistenceManager persistence) {
                    ((polly.core.users.User)user).getRoles().add(role);
                }
            });
        }
    }
    
    

    @Override
    public synchronized void removeRole(final User user, final String roleName) 
            throws RoleException, DatabaseException {
        
        synchronized (SYNC) {
            this.persistence.atomicWriteOperation(new WriteAction() {
                @Override
                public void performUpdate(PersistenceManager persistence) {
                    ((polly.core.users.User)user).getRoles().remove(new Role(roleName));
                }
            });
        }
    }
    
    

    @Override
    public boolean hasPermission(User user, String permission) {
        if (permission.equals(RoleManager.NONE_PERMISSIONS)) {
            return true;
        }
        
        polly.core.users.User puser = (polly.core.users.User) user;
        
        synchronized (SYNC) {
            for (Role role : puser.getRoles()) {
                if (role.getPermissions().contains(permission)) {
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
