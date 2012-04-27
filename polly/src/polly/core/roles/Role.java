package polly.core.roles;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;



@Entity
public class Role {
    
    public final static String ROLE_BY_NAME = "ROLE_BY_NAME"; 

    private int id;
    
    private String name;
    
    private Set<Permission> permissions;
    
    
    
    public Role() {}
    
    
    
    public Role(String name) {
        this(name, new HashSet<Permission>());
    }
    
    
    
    public Role(String name, Set<Permission> permissions) {
        this.name = name;
        this.permissions = permissions;
    }
    
    
    
    
    public int getId() {
        return this.id;
    }
    
    
    
    
    public String getName() {
        return this.name;
    }
    
    
    
    public Set<Permission> getPermissions() {
        return this.permissions;
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Role)) {
            return false;
        }
        Role other = (Role) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }
}