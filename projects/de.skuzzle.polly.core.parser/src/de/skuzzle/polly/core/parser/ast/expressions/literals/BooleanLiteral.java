package de.skuzzle.polly.core.parser.ast.expressions.literals;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.Transformation;
import de.skuzzle.polly.tools.Equatable;

/**
 * Represents a boolean literal and can hold the values <code>true</code> or 
 * <code>false</code>.
 * 
 * @author Simon Taddiken
 */
public class BooleanLiteral extends Literal {
    
    private final boolean value;
    

    /**
     * Creates a new BooleanLiteral.
     * 
     * @param position The position of this literal within the source.
     * @param value The value of this literal.
     */
    public BooleanLiteral(Position position, boolean value) {
        super(position, Type.BOOLEAN);
        this.value = value;
    }
    
    
    
    /**
     * Gets this literal's value.
     * 
     * @return The value of this literal.
     */
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
    
    
    
    @Override
    public Class<?> getEquivalenceClass() {
        return BooleanLiteral.class;
    }
    
    
    
    @Override
    public boolean actualEquals(Equatable o) {
        final BooleanLiteral other = (BooleanLiteral) o;
        return this.value == other.value;
    }
}
