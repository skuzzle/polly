package de.skuzzle.polly.parsing;


public class PollyParserFactory {

    public static InputParser createParser(SyntaxMode mode) {
        switch (mode) {
        case POLLY_CLASSIC: return new InputParser();
        case POLLY_V_07:    return new PollyV07InputParser();
        default: assert false;
        }
        
        return null; // never happen
    }
    
    

    public static InputParser createParser(String syntaxMode) {
        if (syntaxMode.equals(SyntaxMode.POLLY_CLASSIC.toString())) {
            return new InputParser();
        } else if (syntaxMode.equals(SyntaxMode.POLLY_V_07.toString())) {
            return new PollyV07InputParser();
        } else {
            throw new IllegalArgumentException("unknown syntax mode: " + syntaxMode);
        }
    }
}