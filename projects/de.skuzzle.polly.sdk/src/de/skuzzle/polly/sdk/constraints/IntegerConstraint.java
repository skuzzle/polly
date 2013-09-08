package de.skuzzle.polly.sdk.constraints;

import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.NumberType;

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
    public boolean accept(Types value) {
        return value instanceof Types.NumberType && ((NumberType) value).isInteger();
    }
}
