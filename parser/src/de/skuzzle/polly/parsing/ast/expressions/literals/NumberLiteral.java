package de.skuzzle.polly.parsing.ast.expressions.literals;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.types.Type;


public class NumberLiteral extends Literal {

    private final double value;
    
    public NumberLiteral(Position position, double value) {
        super(position, Type.NUMBER);
        this.value = value;
    }
    
    
    
    public double getValue() {
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

    
    
    public int compareTo(Literal o) {
        if (o instanceof NumberLiteral) {
            return Double.compare(this.value, ((NumberLiteral) o).value);
        }
        return super.compareTo(o);
    };
}
