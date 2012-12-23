package de.skuzzle.polly.parsing.ast.expressions.literals;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;


public class StringLiteral extends Literal {

    private static final long serialVersionUID = 1L;
    
    private final String value;
    
    
    public StringLiteral(Position position, String value) {
        super(position, Type.STRING);
        this.value = value;
    }
    
    
    
    protected StringLiteral(Position position, String value, Type type) {
        super(position, type);
        this.value = value;
    }
    
    
    
    public String getValue() {
        return this.value;
    }

    

    @Override
    public Literal castTo(Type type) throws ASTTraversalException {
        return super.castTo(type);
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
