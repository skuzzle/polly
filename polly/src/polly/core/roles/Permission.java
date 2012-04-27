package polly.core.roles;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "ALL_PERMISSIONS", query = "SELECT p FROM Permission p")
public class Permission {
    
    public final static String ALL_PERMISSIONS = "ALL_PERMISSIONS";
    
    public final static String PERMISSION_BY_NAME = "PERMISSION_BY_NAME";

    private int id;
    
    private String name;
    
    
    public Permission() {}
    
    public Permission(String name) {
        this.name = name;
    }
    
    
    
    public int getId() {
        return this.id;
    }
    
    
    
    public String getName() {
        return this.name;
    }
}
