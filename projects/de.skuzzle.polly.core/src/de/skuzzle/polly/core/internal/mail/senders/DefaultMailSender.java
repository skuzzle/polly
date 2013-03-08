package de.skuzzle.polly.core.internal.mail.senders;

import java.util.Properties;

import de.skuzzle.polly.core.internal.mail.MailConfig;




public class DefaultMailSender extends MailSender {

    public DefaultMailSender(MailConfig config) {
        super(config);
    }
    
    
    @Override
    protected Properties createProperties() {
        Properties props = super.createProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", this.config.readString(MailConfig.SMTP_HOST));
        props.put("mail.smtp.port", this.config.readString(MailConfig.SMTP_PORT));
        return props;
    }

}
