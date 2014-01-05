package polly.rx;

import java.util.regex.Pattern;

import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.ListType;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.constraints.AttributeConstraint;


public class SectorListAttributeConstraint implements AttributeConstraint {

    private final static Pattern ACCEPT = Pattern.compile("[-a-z0-9 ]+ \\d+ \\d+",  //$NON-NLS-1$
            Pattern.CASE_INSENSITIVE);
    
    @Override
    public boolean accept(Types type) {
        if (type instanceof Types.ListType) {
            final ListType lt = (ListType) type;
            if (lt.getElementType() instanceof StringType) {
                for (final Types t : lt.getElements()) {
                    final StringType st = (StringType) t;
                    if (!ACCEPT.matcher(st.getValue()).matches()) {
                        return false;
                    }
                }
                return true;
            }
        } else if (type instanceof StringType){
            return ((StringType) type).getValue().equals(""); //$NON-NLS-1$
        }
        
        return false;
    }

}
