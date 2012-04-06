package polly.core.mail;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.internet.InternetAddress;

import polly.configuration.ConfigurationFileException;


public class MailConfig extends Properties {

    private static final long serialVersionUID = 1L;
    
    public final static String SMTP_HOST = "mail.smtp.host";
    public final static String SMTP_PORT = "mail.smtp.port";
    public final static String SMTP_AUTH = "mail.smtp.auth";
    
    public final static String SMTP_LOGIN = "mail.smtp.login";
    public final static String SMTP_PASSWORD = "mail.smtp.password";
    public final static String SMTP_SENDER = "mail.smtp.sender";
    
    public final static String RECIEPIENTS = "recipients";
    public final static String LOG_THRESHOLD = "threshold";
    
    
    private List<InternetAddress> recipients;
    
    public MailConfig(String cfgPath) throws IOException, ConfigurationFileException {
        FileInputStream input = new FileInputStream(cfgPath);
        this.load(input);
        
        // parse recipients
        String[] parts = this.getProperty(RECIEPIENTS).split(";");
        this.recipients = new ArrayList<InternetAddress>(parts.length);
        
        for (String part : parts) {
            try {
                this.recipients.add(new InternetAddress(part));
            } catch (Exception e) {
                // skip invalid mail
                continue;
            }
        }
        
        if (this.recipients.isEmpty()) {
            throw new ConfigurationFileException("no valid recipients");
        }
    }
    
    
    
    public List<InternetAddress> getRecipients() {
        return this.recipients;
    }
}
