package de.skuzzle.polly.sdk.constraints;

/**
 * This constraint only accepts strings that are formatted as integers.
 * 
 * @author Simon
 * @since 0.7
 */
public class IntegerConstraint implements AttributeConstraint {

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
