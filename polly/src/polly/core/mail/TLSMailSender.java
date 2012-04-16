package polly.core.mail;

import java.util.Properties;



public class TLSMailSender extends DefaultMailSender {

    public TLSMailSender(MailConfig config) {
        super(config);
    }
    
    
    
    @Override
    protected Properties createProperties() {
        Properties props = super.createProperties();
        props.put("mail.smtp.starttls.enable", "true");
        
        return props;
    }
}