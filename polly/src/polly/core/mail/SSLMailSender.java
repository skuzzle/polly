package polly.core.mail;

import java.util.Properties;


public class SSLMailSender extends MailSender {


    public SSLMailSender(MailConfig config) {
        super(config);
    }
    
    
    
    @Override
    protected Properties createProperties() {
        Properties props = super.createProperties();
        props.put("mail.smtp.host", this.config.getProperty(MailConfig.SMTP_HOST));
        props.put("mail.smtp.socketFactory.port", 
                this.config.getProperty(MailConfig.SMTP_PORT));
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", this.config.getProperty(MailConfig.SMTP_PORT));
        
        return props;
    }
}