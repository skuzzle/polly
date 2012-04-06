package polly.core.mail;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;


public class EMailLogAppender extends AppenderSkeleton {

    private final static String SUBJECT = "[POLLY Logging] Encountered LogEvent with Level %s";
    private final static String MESSAGE = 
        "Polly encountered a logevent that was above or equal to the mail notification " +
        "threshold\n\n Logmessage: %s\n\nException trace: %s";
    
    private MailSender sender;
    private Level threshold;
    private String subject;
    
    
    
    public EMailLogAppender(MailSender sender, Level threshold) {
        this.sender = sender;
        this.threshold = threshold;
        this.subject = String.format(SUBJECT, threshold.toString());
    }
    
    
    
    @Override
    public void close() {}
    
    

    @Override
    public boolean requiresLayout() {
        return false;
    }
    
    

    @Override
    protected void append(LoggingEvent le) {
        if (le.getLevel().isGreaterOrEqual(this.threshold)) {
            String exception = "none";
            
            // XXX: second condition may not be needed
            if (le.getThrowableInformation() != null && le.getThrowableInformation().getThrowable() != null) {
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
            String message = String.format(MESSAGE, (String) le.getMessage(), exception);
            
            try {
                this.sender.sendMail(message, this.subject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
