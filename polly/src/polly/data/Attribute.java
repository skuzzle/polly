package polly.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
    @NamedQuery(
        name = "ALL_ATTRIBUTES",
        query = "SELECT a FROM Attribute a"),
    @NamedQuery(
        name = "ATTRIBUTE_BY_NAME",
        query = "SELECT a FROM Attribute a WHERE a.name = ?1")
})
public class Attribute {
    
    public final static String ALL_ATTRIBUTES = "ALL_ATTRIBUTES";
    
    public final static String ATTRIBUTE_BY_NAME = "ATTRIBUTE_BY_NAME";
    

    @Id@GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    
    @Column(unique = true)
    private String name;
    
    private String defaultvalue;
    
    public Attribute() {}
    
    public Attribute(String name, String defaultvalue) {
        this.name = name;
        this.defaultvalue = defaultvalue;
    }
    
    
    
    public int getId() {
        return this.id;
    }
    
    

    public String getName() {
        return this.name;
    }


    
    public String getDefaultValue() {
        return this.defaultvalue;
    }
    
    
    
    @Override
    public String toString() {
        return "Name: " + this.name + ", Defaultvalue: " + this.defaultvalue;
    }
}
