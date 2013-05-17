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
    public final static String REPORT_UNKNOWN_VARIABLES = "reportUnknown";
    
    /**
     * Whether the parser should accept polymorphic declarations (declarations with no
     * type explicitly specified).
     */
    public final static String ALLOW_POLYMORPHIC_DECLS = "allowPolymorphic";
    
    /** Whether subtyping should be taken into account while unifying types. */
    public final static String ALLOW_SUBTYPING = "allowSubtyping";
    
    /** Whether the parser should accept escaped tokens as identifiers. */
    public final static String ENABLE_TOKEN_ESCAPING = "enableEscaping";
    
    /** Whether all read tokens should be printed to std out. */
    public final static String ENABLE_SCANNER_DEBUGGING = "scannerDebugging";

    /** Whether execution debuggin should be enabled. */
    public static final String ENABLE_EXECUTION_DEBUGGING = "execDebugging";
    
    
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
            throw new NullPointerException("property '" + property + "' not assigned");
        } else if (value.equals(Boolean.TRUE.toString())) {
            return true;
        } else if (value.equals(Boolean.FALSE.toString())) {
            return false;
        } else {
            throw new IllegalArgumentException(
                "property '" + property + "' is no boolean");
        }
    }
    
    
    
    private ParserProperties() {}
}
