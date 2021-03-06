package de.skuzzle.polly.core.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides static method to read some crucial parser settings.
 * 
 * @author Simon Taddiken
 */
public class ParserProperties {

    /** 
     * Whether unknown variables should be reported (true) or treated as string (false). 
     */
    public final static String REPORT_UNKNOWN_VARIABLES = "reportUnknown"; //$NON-NLS-1$
    
    /**
     * Whether the parser should accept polymorphic declarations (declarations with no
     * type explicitly specified).
     */
    public final static String ALLOW_POLYMORPHIC_DECLS = "allowPolymorphic"; //$NON-NLS-1$
    
    /** Whether subtyping should be taken into account while unifying types. */
    public final static String ALLOW_SUBTYPING = "allowSubtyping"; //$NON-NLS-1$
    
    /** Whether the parser should accept escaped tokens as identifiers. */
    public final static String ENABLE_TOKEN_ESCAPING = "enableEscaping"; //$NON-NLS-1$
    
    /** Whether all read tokens should be printed to std out. */
    public final static String ENABLE_SCANNER_DEBUGGING = "scannerDebugging"; //$NON-NLS-1$

    /** Whether execution debuggin should be enabled. */
    public static final String ENABLE_EXECUTION_DEBUGGING = "execDebugging"; //$NON-NLS-1$
    
    /** Minimum length of a command name */
    public final static String COMMAND_MIN_LENGTH = "commandMinLength"; //$NON-NLS-1$
    
    
    /** Stores the parser properties. */
    private final static Map<String, String> properties;

    static {
        // init with default settings
        properties = new HashMap<String, String>();
        properties.put(REPORT_UNKNOWN_VARIABLES, Boolean.FALSE.toString());
        properties.put(ALLOW_POLYMORPHIC_DECLS, Boolean.FALSE.toString());
        properties.put(ALLOW_SUBTYPING, Boolean.FALSE.toString());
        properties.put(ENABLE_TOKEN_ESCAPING, Boolean.TRUE.toString());
        properties.put(ENABLE_SCANNER_DEBUGGING, Boolean.FALSE.toString());
        properties.put(ENABLE_EXECUTION_DEBUGGING, Boolean.FALSE.toString());
        properties.put(COMMAND_MIN_LENGTH, "2"); //$NON-NLS-1$
    }
    
    
    
    /**
     * Reads a boolean parser property, throws exceptions if the property name is not 
     * assigned or does not evaluate to a boolean value.
     *  
     * @param property Name of the property to retrieve.
     * @return <code>true</code> iff the value of the property is "true".
     */
    public final static boolean should(String property) {
        final String value = properties.get(property);
        if (value == null) {
            throw new NullPointerException("property '" + property + "' not assigned"); //$NON-NLS-1$ //$NON-NLS-2$
        } else if (value.equals(Boolean.TRUE.toString())) {
            return true;
        } else if (value.equals(Boolean.FALSE.toString())) {
            return false;
        } else {
            throw new IllegalArgumentException(
                "property '" + property + "' is no boolean"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    
    
    
    public final static int getInt(String property) {
        return Integer.parseInt(properties.get(property));
    }
    
    
    
    private ParserProperties() {}
}
