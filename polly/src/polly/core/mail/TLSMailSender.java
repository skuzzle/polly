package polly.core.mail;

import java.util.Properties;



public class TLSMailSender extends MailSender {

    public TLSMailSender(MailConfig config) {
        super(config);
    }
    
    
    
    @Override
    protected Properties createProperties() {
        Properties props = super.createProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", this.config.getProperty(MailConfig.SMTP_HOST));
        props.put("mail.smtp.port", this.config.getProperty(MailConfig.SMTP_PORT));
        
        return props;
    }
}