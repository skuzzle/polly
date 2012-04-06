package polly.core.mail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {

    private Properties props;
    private MailConfig config;
    private InternetAddress[] reciepients;
    
    
    
    public MailSender(MailConfig config) {
        this.config = config;
        
        this.props = new Properties();
        this.props.put(MailConfig.SMTP_HOST, config.getProperty(MailConfig.SMTP_HOST));
        this.props.put(MailConfig.SMTP_PORT, config.getProperty(MailConfig.SMTP_PORT));
        this.props.put(MailConfig.SMTP_AUTH, config.getProperty(MailConfig.SMTP_AUTH));
        this.reciepients = new InternetAddress[config.getRecipients().size()];
        this.reciepients = config.getRecipients().toArray(this.reciepients);
    }
    
    
    
    public void sendMail(String message, String subject) throws AddressException, MessagingException {
        Session session = Session.getDefaultInstance(this.props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    config.getProperty(MailConfig.SMTP_LOGIN), 
                    config.getProperty(MailConfig.SMTP_PASSWORD));
            }
        });
        
        Message m = new MimeMessage(session);
        m.setFrom(new InternetAddress(this.config.getProperty(MailConfig.SMTP_SENDER)));
        m.setRecipients(Message.RecipientType.TO, this.reciepients);
        m.setSubject(subject);
        m.setText(message);
        
        Transport.send(m);
    }
}