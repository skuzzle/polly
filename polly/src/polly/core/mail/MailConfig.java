package polly.core.mail;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Properties;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.log4j.Level;


import polly.configuration.ConfigurationFileException;


public class MailConfig extends Properties {

    private static final long serialVersionUID = 1L;
    
    public final static String SMTP_FROM = "mail.smtp.from";
    public final static String SMTP_HOST = "mail.smtp.host";
    public final static String SMTP_PORT = "mail.smtp.port";
    public final static String SMTP_PASSWORD = "mail.smtp.password";
    public final static String SMTP_LOGIN = "mail.smtp.login";
    
    public final static String RECIPIENTS = "recipients";
    public final static String LOG_THRESHOLD = "threshold";
    public final static String MAIL_PROVIDER = "mailProvider";
    public final static String MAIL_DELAY = "mailDelay";
    
    private InternetAddress[] recipients;
    private MailSender sender;
    private Level logLevel;
    
    
    
    public MailConfig(String cfgPath) throws IOException, ConfigurationFileException {
        FileInputStream input = new FileInputStream(cfgPath);
        this.load(input);
        
        // parse recipients
        try {
            this.recipients = InternetAddress.parse(this.getProperty(RECIPIENTS), false);
        } catch (AddressException e) {
            throw new ConfigurationFileException(e, e.getMessage());
        }
        
        
        this.logLevel = Level.toLevel(this.getProperty(LOG_THRESHOLD));
        if (this.logLevel.toInt() <= Level.INFO_INT) {
            throw new ConfigurationFileException(
                "Log level threshold too low. Must at least be 'ERROR'.");
        }
        
        
        // init mail provider
        String cls = this.getProperty(MAIL_PROVIDER);
        try {
            Class<?> provider = Class.forName(cls);
            Constructor<?> con = provider.getConstructor(MailConfig.class);
            Object tmp = con.newInstance(this);
            
            if (!(tmp instanceof MailSender)) {
                throw new Exception(cls + " is no valid MailSender");
            }
            this.sender = (MailSender) tmp;
        } catch (Exception e) {
            throw new ConfigurationFileException(e, "Invalid MailProvider: " + cls);
        }
    }
    
    
    
    public MailSender getSender() {
        return this.sender;
    }
    
    
    
    public InternetAddress[] getRecipients() {
        return this.recipients;
    }



    public Level getLevel() {
        return this.logLevel;
    }
}
