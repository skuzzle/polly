package polly.core.irc;

import java.util.Properties;

import polly.core.mail.MailConfig;
import polly.core.mail.MailSender;


public class DefaultMailSender extends MailSender {

    public DefaultMailSender(MailConfig config) {
        super(config);
    }
    
    
    @Override
    protected Properties createProperties() {
        Properties props = super.createProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", this.config.getProperty(MailConfig.SMTP_HOST));
        props.put("mail.smtp.port", this.config.getProperty(MailConfig.SMTP_PORT));
        return props;
    }

}
