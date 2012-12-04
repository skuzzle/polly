package de.skuzzle.polly.parsing;


public class EscapedToken extends Token {

    private static final long serialVersionUID = 1L;

    
    private final Token escaped;
    
    
    public EscapedToken(Position position, Token escaped) {
        super(TokenType.ESCAPED, position);
        this.escaped = escaped;
    }
    
    
    
    public Token getEscaped() {
        return this.escaped;
    }
}
