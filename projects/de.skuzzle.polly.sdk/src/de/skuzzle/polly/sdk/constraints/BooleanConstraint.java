package de.skuzzle.polly.sdk.constraints;

/**
 * <p>This constraint only accepts strings that represent boolean values. That is, it only
 * accepts <code>"true"</code> and <code>"false"</code>.</p>
 * 
 * <p>You can get an instance of this constraint using {@link Constraints#BOOLEAN}</p>
 * 
 * @author Simon
 * @since 0.7
 */
public class BooleanConstraint implements AttributeConstraint {

    BooleanConstraint() {}
    
    
    @Override
    public boolean accept(String value) {
        return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
    }

}
