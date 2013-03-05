package de.skuzzle.polly.core.parser.ast.expressions.literals;

import java.util.Date;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.Transformation;

/**
 * Represents a date literal.
 * 
 * @author Simon Taddiken
 */
public class DateLiteral extends Literal {
    
    private final Date value;
    
    /**
     * Creates a new DateLiteral.
     * 
     * @param position The position of this literal within the source.
     * @param value The value of this literal.
     */
    public DateLiteral(Position position, Date value) {
        super(position, Type.DATE);
        this.value = value;
    }
    
    
    
    /**
     * Custom constructor to create {@link TimespanLiteral TimespanLiterals}.
     * 
     * @param position The position of this literal within the source.
     * @param value The value of this literal.
     * @param type type of this literal.
     */
    protected DateLiteral(Position position, Date value, Type type) {
        super(position, type);
        this.value = value;
    }
    
    
    
    /**
     * Gets this literal's value.
     * 
     * @return The value of this literal.
     */
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
    
    
    
    @Override
    public Class<?> getEquivalenceClass() {
        return DateLiteral.class;
    }
    
    
    
    @Override
    public boolean actualEquals(Equatable o) {
        final DateLiteral other = (DateLiteral) o;
        return this.value.equals(other.value);
    }
}
