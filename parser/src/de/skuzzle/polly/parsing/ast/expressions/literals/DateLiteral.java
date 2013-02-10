package de.skuzzle.polly.parsing.ast.expressions.literals;

import java.util.Date;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Transformation;


public class DateLiteral extends Literal {

    private static final long serialVersionUID = 1L;
    
    private final Date value;
    
    
    public DateLiteral(Position position, Date value) {
        super(position, Type.DATE);
        this.value = value;
    }
    
    
    
    protected DateLiteral(Position position, Date value, Type type) {
        super(position, type);
        this.value = value;
    }
    
    
    
    public Date getValue() {
        return this.value;
    }

    
    
    @Override
    public Literal castTo(Type type) throws ASTTraversalException {
        if (type.equals(Type.NUM)) {
            return new NumberLiteral(this.getPosition(), this.getValue().getTime());
        }
        return super.castTo(type);
    }
    
    
    
    @Override
    public int compareTo(Literal o) {
        if (o instanceof DateLiteral) {
            final DateLiteral other = (DateLiteral) o;
            return (int) (this.value.getTime() - other.value.getTime());
        }
        return super.compareTo(o);
    }

    
    
    @Override
    public String format(LiteralFormatter formatter) {
        return formatter.formatDate(this);
    }
    
    
    
    @Override
    public Expression transform(Transformation transformation) 
            throws ASTTraversalException {
        return transformation.transformDate(this);
    }
}
