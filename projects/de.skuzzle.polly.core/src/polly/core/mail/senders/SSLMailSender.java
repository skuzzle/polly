package polly.core.mail.senders;

import java.util.Properties;

import polly.core.mail.MailConfig;


public class SSLMailSender extends DefaultMailSender {


    public SSLMailSender(MailConfig config) {
        super(config);
    }
    
    
    
    @Override
    protected Properties createProperties() {
        Properties props = super.createProperties();
        props.put("mail.smtp.socketFactory.port", 
                this.config.readString(MailConfig.SMTP_PORT));
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        
        return props;
    }
}