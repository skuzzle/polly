package de.skuzzle.polly.sdk;

/**
 * Represents an user attribute which can be assigned to a user using the 
 * {@link UserManager}
 * 
 * @author Simon Taddiken
 */
public interface Attribute {

    public String getName();
    
    public String getDescription();
    
    public String getCategory();
    
    public String getDefaultValue();
}
