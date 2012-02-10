package de.skuzzle.polly.sdk.constraints;


/**
 * This constraint only accepts strings that are formatted as doubles.
 * 
 * @author Simon
 * @since 0.7
 */
public class DoubleConstraint implements AttributeConstraint {

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
