package polly.core.roles;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
    @NamedQuery(name = "ALL_PERMISSIONS", query = "SELECT p FROM Permission p"),
    @NamedQuery(name = "PERMISSION_BY_NAME", query = "SELECT p FROM Permission p WHERE p.name = ?1")
})
public class Permission {
    
    public final static String ALL_PERMISSIONS = "ALL_PERMISSIONS";
    
    public final static String PERMISSION_BY_NAME = "PERMISSION_BY_NAME";

    @Id@GeneratedValue(strategy = GenerationType.TABLE)
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
