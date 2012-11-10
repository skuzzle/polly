package de.skuzzle.polly.parsing.ast.expressions.literals;

import java.util.Calendar;
import java.util.Date;

import de.skuzzle.polly.parsing.Position;


public class TimespanLiteral extends DateLiteral {
    
    private final static Date addSeconds(Date date, int seconds) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.SECOND, seconds);
        return c.getTime();
    }

    
    private final int seconds;
    
    
    public TimespanLiteral(Position position, int seconds) {
        super(position, addSeconds(new Date(), seconds));
        this.seconds = seconds;
    }

    
    
    public int getSeconds() {
        return this.seconds;
    }
}
