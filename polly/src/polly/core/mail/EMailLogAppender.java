package polly.core.mail;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;


public class EMailLogAppender extends AppenderSkeleton {

    private final static String SUBJECT = 
        "[POLLY Logging] Encountered LogEvent with level '%s'";
    
    private final static String MESSAGE = 
        "Polly encountered a LogEvent that was above or equal to the mail notification " +
        "threshold '%s'.\n\n LogMessage: %s\n Exception trace: %s";
    
    
    
    private MailSender sender;
    private Level threshold;
    private String subject;
    private long lastSent;
    private int delay;
    
    
    
    public EMailLogAppender(MailSender sender, Level threshold, int delay) {
        this.sender = sender;
        this.threshold = threshold;
        this.subject = String.format(SUBJECT, threshold.toString());
        this.delay = delay;
    }
    
    
    
    @Override
    public void close() {}
    
    

    @Override
    public boolean requiresLayout() {
        return false;
    }
    
    

    @Override
    protected void append(LoggingEvent le) {
        if (System.currentTimeMillis() - this.lastSent < this.delay) {
            return;
        }
        
        if (le.getLevel().isGreaterOrEqual(this.threshold)) {
            this.lastSent = System.currentTimeMillis();
            String exception = "none";
            
            // XXX: second condition may not be needed
            if (le.getThrowableInformation() != null && 
                            le.getThrowableInformation().getThrowable() != null) {
                
                Throwable e = le.getThrowableInformation().getThrowable();
                StringBuilder b = new StringBuilder();
                
                b.append(e.getMessage());
                b.append("\n");
                for (StackTraceElement ste : e.getStackTrace()) {
                    b.append("    ");
                    b.append(ste.toString());
                    b.append("\n");
                }
                
                exception = b.toString();
            }
            String message = String.format(MESSAGE, 
                this.threshold, (String) le.getMessage(), exception);
            
            try {
                this.sender.sendMail(this.subject, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
