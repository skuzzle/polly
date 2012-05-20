package de.skuzzle.polly.parsing.tree.literals;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.TokenType;
import de.skuzzle.polly.parsing.types.Type;


public class HelpLiteral extends Literal {

    private static final long serialVersionUID = 1L;


    
    public HelpLiteral() {
        super(new Token(TokenType.QUESTION, Position.EMPTY), Type.HELP);
    }
    
    

    public HelpLiteral(Token token) {
        super(token, Type.HELP);
    }
    
    
    
    @Override
    public int compareTo(Literal o) {
        throw new UnsupportedOperationException();
    }

    
    
    public String toString() {
        return this.getType().getTypeName().getIdentifier();
    };
}
