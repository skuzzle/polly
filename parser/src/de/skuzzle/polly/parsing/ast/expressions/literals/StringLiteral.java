package de.skuzzle.polly.parsing.ast.expressions.literals;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.types.Type;


public class StringLiteral extends Literal {

    private final String value;
    
    
    public StringLiteral(Position position, String value) {
        super(position, Type.STRING);
        this.value = value;
    }
    
    
    
    public String getValue() {
        return this.value;
    }

    

    @Override
    public Literal castTo(Type type) throws ASTTraversalException {
        return null;
    }

    
    
    @Override
    public String format(LiteralFormatter formatter) {
        return formatter.formatString(this);
    }
    
    
    
    @Override
    public String toString() {
        return this.value;
    }
}
