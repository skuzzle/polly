package polly.rx;

import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.NumberType;
import de.skuzzle.polly.sdk.constraints.AttributeConstraint;


public class DockLevelConstraint implements AttributeConstraint {

    @Override
    public boolean accept(Types type) {
        return type instanceof NumberType && ((NumberType) type).getValue() > 0;
    }

}
