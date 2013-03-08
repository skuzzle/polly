package de.skuzzle.polly.core.parser.ast.expressions.literals;

import java.util.Calendar;
import java.util.Date;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.Transformation;


public class TimespanLiteral extends DateLiteral {
    
    /**
     * Adds a given amount of seconds to a given date.
     * 
     * @param date The date.
     * @param seconds The seconds to add.
     * @return A new date.
     */
    private final static Date addSeconds(Date date, int seconds) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.SECOND, seconds);
        return c.getTime();
    }

    
    private final int seconds;
    
    
    
    public TimespanLiteral(Position position, int seconds) {
        super(position, addSeconds(new Date(), seconds), Type.TIMESPAN);
        this.seconds = seconds;
    }
    
    
    
    /**
     * Adds this timespan to the given date and returns the target date.
     * 
     * @param d The date to add this timespan to.
     * @return The target date.
     */
    public Date addToDate(Date d) {
        return addSeconds(d, this.getSeconds());
    }
    
    
    
    @Override
    public String format(LiteralFormatter formatter) {
        return formatter.formatTimespan(this);
    }

    
    
    /**
     * Gets the amount of seconds this timespan represents.
     * 
     * @return The seconds.
     */
    public int getSeconds() {
        return this.seconds;
    }
    
    
    
    @Override
    public Literal castTo(Type type) throws ASTTraversalException {
        if (type.equals(Type.DATE)) {
            return new DateLiteral(this.getPosition(), addToDate(new Date()));
        } else if (type == Type.NUM) {
            return new NumberLiteral(this.getPosition(), this.seconds * 1000);
        } else {
            return super.castTo(type);
        }
    }
    
    
    
    @Override
    public Expression transform(Transformation transformation)
            throws ASTTraversalException {
        return transformation.transformTimeSpan(this);
    }
    
    
    
    @Override
    public int compareTo(Literal o) {
        if (o instanceof TimespanLiteral) {
            final TimespanLiteral other = (TimespanLiteral) o;
            return this.seconds - other.seconds;
        }
        return super.compareTo(o);
    }
    
    
    
    @Override
    public String toString() {
        return LiteralFormatter.DEFAULT.formatTimespan(this);
    }
}
