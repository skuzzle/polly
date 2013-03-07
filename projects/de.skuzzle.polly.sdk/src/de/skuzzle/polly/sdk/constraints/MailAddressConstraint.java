package de.skuzzle.polly.sdk.constraints;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <p>This constraint only accepts email addresses formed like the pattern 
 * <code>^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$</code>.</p>
 * 
 * <p>You can get an instance of this constraint using {@link Constraints#MAILADDRESS}</p>
 * 
 * @author Simon
 * @since 0.9
 */
public class MailAddressConstraint implements AttributeConstraint {

    /**
     * A regex pattern that matches on valid formed mail addresses.
     */
    public final static Pattern MAIL_PATTERN = Pattern.compile(
        "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
    
    
    
    MailAddressConstraint() {}
    
    @Override
    public boolean accept(String value) {
        Matcher m = MAIL_PATTERN.matcher(value);
        return m.matches();
    }

}
