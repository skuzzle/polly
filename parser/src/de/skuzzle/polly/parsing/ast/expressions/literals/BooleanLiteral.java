package de.skuzzle.polly.parsing.ast.expressions.literals;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.types.Type;


public class BooleanLiteral extends Literal {
    
    private final boolean value;
    

    public BooleanLiteral(Position position, boolean value) {
        super(position, Type.BOOLEAN);
        this.value = value;
    }
    
    
    
    public boolean getValue() {
        return this.value;
    }
    


    @Override
    public Literal castTo(Type type) throws ASTTraversalException {
        return null;
    }
    
    

    @Override
    public String format(LiteralFormatter formatter) {
        return null;
    }
    
    
    
    @Override
    public String toString() {
        return Boolean.toString(this.value);
    }
}
