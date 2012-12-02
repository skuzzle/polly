package de.skuzzle.polly.parsing.ast.expressions.literals;

import java.util.Calendar;
import java.util.Date;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.types.Type;


public class TimespanLiteral extends DateLiteral {
    
    private static final long serialVersionUID = 1L;
    
    
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
        super(position, addSeconds(new Date(), seconds));
        this.setType(Type.TIMESPAN);
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
    public String toString() {
        return LiteralFormatter.DEFAULT.formatTimespan(this);
    }
}
