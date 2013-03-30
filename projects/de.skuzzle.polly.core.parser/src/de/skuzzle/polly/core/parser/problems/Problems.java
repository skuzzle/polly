package de.skuzzle.polly.core.parser.problems;

/**
 * Defines constants for possible parse errors.
 * 
 * @author Simon Taddiken
 */
public final class Problems {

    /**
     * Formats the given problem message using the given parameters.
     * 
     * @param message Problem message to format.
     * @param params Formatting parameters.
     * @return The formatted problem message.
     */
    public final static String format(String message, Object...params) {
        return String.format(message, params);
    }
    
    
    
    // Lexical errors
    public final static String ILLEGAL_SYMBOL = "Ungültiges Symbol: %s";
    public final static String MISSING_RADIX = "Fehlende Radix Angabe für 0x: Operator";
    public final static String HIGH_RADIX = "Angegebener Radix %d ist zu groß (max: %d)";
    public final static String INVALID_0X = "Ungültiger '0x:' Operator.";
    public final static String INVALID_STRING = "Ungültiger String-Literal.";
    public final static String INVALID_ESCAPE = "Ungültige escape-Sequenz: %s";
    public final static String UNCLOSED_STRING = "Nicht geschlossenes String-Literal.";
    public final static String INVALID_CHANNEL = "Ungültiges Channel-Literal: %s.";
    public final static String INVALID_USER = "Ungültiges User-Literal: %s.";
    public final static String INVALID_IDENTIFIER = "Ungültiger Bezeichner.";
    public final static String INVALID_IDENTIFIER2 = "Ungültiger Bezeichner: %s";
    public final static String MISSING_DECIMALS = "Fehlende Dezimalstellen.";
    public final static String INVALID_DATE_TIME = "Ungültiges DateTime-Literal.";
    public final static String INVALID_NUMBER = "Ungültige Zahl.";
    public final static String INVALID_RADIXED_INT = "Ungültige Zahl mit Radix.";
    
    
    
    // Syntactical errors
    public final static String MISSING_OBR = "Fehlende öffnende Klammer.";
    public final static String UNEXPECTED_TOKEN = "Unerwartetes Symbol: %s, erwartet: %s";
    
    
    
    // Semantical errors
    public final static String NO_FUNCTION = "'%s' ist keine Funktion.";
    public final static String UNKNOWN_FUNCTION = "Funktion '%s' existiert nicht.";
    public final static String INCOMPATIBLE_OP = "Operator '%s' nicht kompatibel mit den angegebenen Typen.";
    public final static String INCOMPATIBLE_CALL = "Keine passende Deklaration für den Aufruf von %s gefunden.";
    public final static String DUPLICATED_DECL = "Doppelte Deklaration von: %s.";
    public final static String EMPTY_LIST = "Listen müssen mind. ein Element enthalten.";
    public final static String RECURSIVE_CALL = "Rekursive Aufrufe sind nicht erlaubt.";
    public final static String ILLEGAL_NS_ACCESS = "Operand muss ein Bezeichner sein.";
    public final static String UNKNOWN_NS = "Unbekannter Namespace: %s.";
    public final static String UNKNOWN_VAR = "Unbekannte Variable: %s.";
    public final static String TYPE_ERROR = "Typefehler. Erwartet: %s, gefunden: %s.";
    public final static String UNKNOWN_TYPE = "Unbekannter Typ: %s.";
    public final static String NOT_ORDERED = "Type %s definiert keine Ordnung.";
    public static final String AMBIGUOUS_CALL = "Nicht eindeutiger Funktionsaufruf.";
    
}
