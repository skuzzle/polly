package de.skuzzle.polly.sdk.httpv2.html;

import java.util.Map;

/**
 * Provides some utility functions regarding HTML pages and template contexts
 * 
 * @author Simon Taddiken
 */
public class HTMLTools {

    interface HTMLToolsUtil {
        public String escape(String s);
        
        public void gainFieldAccess(Map<String, Object> targetContext, 
                Class<?> container, String key);
    }
    
    /** This field is initialized by polly.core. This is a dependency hack */
    static HTMLToolsUtil UTIL;
    
    
    
    /**
     * Escapes all HTML characters contained in the provided string.
     * 
     * @param s The string to escape.
     * @return The escaped string.
     */
    public static String escape(String s) {
        return UTIL.escape(s);
    }
    
    
    
    /**
     * This method allows to gain access to <tt>public static</tt> fields of a certain
     * class within a Velocity template.
     * 
     * @param targetContext The context for which field access will be enabled
     * @param container The class which fields shall be accessible within the context 
     * @param key The key with which the class can be referenced from within a template
     */
    public static void gainFieldAccess(Map<String, Object> targetContext, 
            Class<?> container, String key) {
        
        UTIL.gainFieldAccess(targetContext, container, key);
    }
        
}
