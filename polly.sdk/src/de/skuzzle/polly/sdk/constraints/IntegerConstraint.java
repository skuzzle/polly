package de.skuzzle.polly.sdk.constraints;

/**
 * <p>This constraint only accepts strings that are formatted as integers.</p>
 * 
 * <p>You can get an instance of this constraint using {@link Constraints#INTEGER}</p>
 * 
 * @author Simon
 * @since 0.7
 */
public class IntegerConstraint implements AttributeConstraint {

    
    IntegerConstraint() {}
    
    
    @Override
    public boolean accept(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
