package de.skuzzle.polly.core.parser;

import de.skuzzle.polly.core.parser.problems.Problems;

/**
 * Exception indicating a syntax error during parsing.
 * 
 * @author Simon Taddiken
 */
public class SyntaxException extends ParseException {

    private static final long serialVersionUID = 1L;
    
    private final TokenType expected;
    private final Token found;
    
    
    /**
     * Creates a new SyntaxException.
     * 
     * @param expected The symbol that was expected.
     * @param found The symbol that was found instead.
     * @param position Position of this error.
     */
    public SyntaxException(TokenType expected, Token found, Position position) {
        super(Problems.format(Problems.UNEXPECTED_TOKEN, 
            found.toString(false, false), expected.toString()), position);
        this.expected = expected;
        this.found = found;
    }
    
    

    /**
     * Gets the {@link TokenType} that was expected where this exception 
     * occurred.
     * 
     * @return The expected token type.
     */
    public TokenType getExpected() {
        return this.expected;
    }

    

    /**
     * Gets the token that was found instead of the expected one.
     * 
     * @return The found token.
     */
    public Token getFound() {
        return this.found;
    }
}
