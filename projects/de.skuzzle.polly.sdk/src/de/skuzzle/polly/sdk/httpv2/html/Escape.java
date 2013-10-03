package de.skuzzle.polly.sdk.httpv2.html;

/**
 * Provides utility to escape HTML characters within a string.
 * 
 * @author Simon Taddiken
 */
public class Escape {

    interface EscapeUtil {
        public String escape(String s);
    }
    
    /** This field is initialized by polly.core. This is a dependency hack */
    static EscapeUtil ESCAPER;
    
    
    
    /**
     * Escapes all HTML characters contained in the provided string.
     * 
     * @param s The string to escape.
     * @return The escaped string.
     */
    public static String html(String s) {
        return ESCAPER.escape(s);
    }
}
