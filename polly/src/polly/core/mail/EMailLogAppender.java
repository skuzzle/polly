package polly.core.mail;


import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;



public class EMailLogAppender extends AppenderSkeleton {

    private final static String SUBJECT = 
        "[POLLY Logging] Encountered LogEvent with level '%s'";
    
    
    private MailSender sender;
    private Level threshold;
    private String subject;
    private long lastSent;
    private int delay;
    private Layout formatter;
    
    
    public EMailLogAppender(MailSender sender, Level threshold, int delay, 
            Layout formatter) {
        this.sender = sender;
        this.threshold = threshold;
        this.subject = String.format(SUBJECT, threshold.toString());
        this.delay = delay;
        this.formatter = formatter;
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
            String message = this.formatter.format(le);
            
            try {
                this.sender.sendMail(this.subject, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
