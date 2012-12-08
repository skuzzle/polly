package de.skuzzle.polly.parsing;

import de.skuzzle.polly.tools.Equatable;


public class EscapedToken extends Token {

    private static final long serialVersionUID = 1L;

    private final Token escaped;
    
    
    public EscapedToken(Position position, Token escaped) {
        super(TokenType.ESCAPED, position);
        this.escaped = escaped;
    }
    
    
    
    @Override
    public Class<?> getEquivalenceClass() {
        return EscapedToken.class;
    }
    
    
    
    @Override
    public boolean actualEquals(Equatable o) {
        final EscapedToken other = (EscapedToken) o;
        return super.actualEquals(o) && this.escaped.equals(other.escaped);
    }
    
    
    
    public Token getEscaped() {
        return this.escaped;
    }
}
