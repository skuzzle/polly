package de.skuzzle.polly.core.internal.mail.senders;

import java.util.Properties;

import de.skuzzle.polly.core.internal.mail.MailConfig;




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