package de.skuzzle.polly.parsing.ast.expressions.literals;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Transformation;


public class BooleanLiteral extends Literal {
    
    private static final long serialVersionUID = 1L;
    
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
        if(type.equals(Type.NUM)) {
            double value = this.getValue() ? 1.0 : 0.0;
            return new NumberLiteral(this.getPosition(), value);
        } else if (type.equals(Type.STRING)) {
            return new StringLiteral(this.getPosition(),
                Boolean.toString(this.getValue()));
        }
        return super.castTo(type);
    }
    
    

    @Override
    public String format(LiteralFormatter formatter) {
        return this.value ? "true" : "false";
    }
    
    
    
    @Override
    public String toString() {
        return Boolean.toString(this.value);
    }
    
    
    
    @Override
    public Expression transform(Transformation transformation) 
            throws ASTTraversalException {
        return transformation.transformBoolean(this);
    }
}
