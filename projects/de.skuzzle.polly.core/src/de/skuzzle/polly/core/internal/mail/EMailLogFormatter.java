package de.skuzzle.polly.core.internal.mail;

import java.io.PrintWriter;
import java.util.Date;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import de.skuzzle.polly.core.util.StringBuilderWriter;



public class EMailLogFormatter extends Layout {
    
    
    private final static String MESSAGE = 
        "Hi admin!\n\nPolly encountered a LogEvent that was above or equal to the mail " +
        "notification threshold '%s'.\n\n " +
        "Time: %s\n " +
        "LogLevel: %s\n " +
        "Logger: %s\n " +
        "Thread: %s\n " +
        "LogMessage: %s\n " +
        "Exception trace: %s";
    
    private Level threshold;
    
    
    
    public EMailLogFormatter(Level threshold) {
        this.threshold = threshold;
    }
    
    
    
    @Override
    public void activateOptions() {}

    
    
    @Override
    public String format(LoggingEvent e) {
        String exception = "none";
        if (e.getThrowableInformation() != null && 
                    e.getThrowableInformation().getThrowable() != null) {
            
            StringBuilder b = new StringBuilder();
            StringBuilderWriter writer = new StringBuilderWriter(b);
            e.getThrowableInformation().getThrowable().printStackTrace(
                new PrintWriter(writer));
            exception = b.toString();
        }
        
        return String.format(MESSAGE, 
            this.threshold,
            new Date(e.getTimeStamp()),
            e.getLevel(),
            e.getLoggerName(),
            e.getThreadName(),
            e.getMessage(),
            exception);
    }

    
    
    @Override
    public boolean ignoresThrowable() {
        return false;
    }
}
