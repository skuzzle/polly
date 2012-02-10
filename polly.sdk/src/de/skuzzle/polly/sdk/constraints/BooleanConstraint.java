package de.skuzzle.polly.sdk.constraints;

/**
 * This constraint only accepts strings that represent boolean values. That is, it only
 * accepts <code>"true"</code> and <code>"false"</code>.
 * 
 * @author Simon
 * @since 0.7
 */
public class BooleanConstraint implements AttributeConstraint {

    @Override
    public boolean accept(String value) {
        return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
    }

}
