package de.skuzzle.polly.sdk.constraints;


/**
 * <p>This constraint only accepts strings that are formatted as doubles.</p>
 * 
 * <p>You can get an instance of this constraint using {@link Constraints#DOUBLE}</p>
 * 
 * @author Simon
 * @since 0.7
 */
public class DoubleConstraint implements AttributeConstraint {

    
    DoubleConstraint() {}
    
    @Override
    public boolean accept(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
