package de.skuzzle.polly.core.internal.users;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import de.skuzzle.polly.sdk.Attribute;

@Entity(name = "AttributeV2")
@NamedQueries({
    @NamedQuery(
        name = AttributeImpl.ALL_ATTRIBUTES,
        query = "SELECT a FROM AttributeV2 a"),
    @NamedQuery(
        name = AttributeImpl.ATTRIBUTE_BY_NAME,
        query = "SELECT a FROM AttributeV2 a WHERE a.name = ?1")
})
class AttributeImpl implements Attribute {
    
    public final static String ALL_ATTRIBUTES = "ALL_ATTRIBUTESV2";
    
    public final static String ATTRIBUTE_BY_NAME = "ATTRIBUTEV2_BY_NAME";
    

    @Id@GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    
    @Column(unique = true)
    private String name;
    
    private String category;
    
    private String defaultvalue;
    
    private String description;
    
    public AttributeImpl() {}
    
    public AttributeImpl(String name, String defaultvalue, String description, 
            String category) {
        this.name = name;
        this.category = category;
        this.defaultvalue = defaultvalue;
        this.description = description;
    }
    
    
    
    public int getId() {
        return this.id;
    }
    
    
    
    public String getCategory() {
        return this.category;
    }
    
    

    public String getName() {
        return this.name;
    }


    
    public String getDescription() {
        return this.description;
    }
    
    
    
    public String getDefaultValue() {
        return this.defaultvalue;
    }
    
    
    
    @Override
    public String toString() {
        return "Name: " + this.name + ", Defaultvalue: " + this.defaultvalue;
    }
}
