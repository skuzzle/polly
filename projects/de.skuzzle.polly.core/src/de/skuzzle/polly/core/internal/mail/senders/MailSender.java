package de.skuzzle.polly.core.internal.mail.senders;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import de.skuzzle.polly.core.internal.mail.MailConfig;



public abstract class MailSender {

    protected final static Logger logger = Logger.getLogger(MailSender.class.getName());
    protected MailConfig config;
    
    
    public MailSender(MailConfig config) {
        this.config = config;
    }
    
    
    
    protected Properties createProperties() {
        return new Properties();
    }
    
    
    
    public void sendMail(String recipient, String subject, String message) 
                throws MessagingException {
        
        Properties props = this.createProperties();
        
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    config.readString(MailConfig.SMTP_LOGIN), 
                    config.readString(MailConfig.SMTP_PASSWORD));
            }
        });
        
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(this.config.readString(MailConfig.SMTP_FROM)));
        msg.setRecipient(RecipientType.TO, new InternetAddress(recipient, false));
        msg.setSubject(subject);
        msg.setText(message);
        
        logger.trace("Sending mail...");
        Transport.send(msg);
        logger.trace("Mail sent");
    }
    
    
    
    public void sendMail(String subject, String message) 
            throws MessagingException {
        
        Properties props = this.createProperties();
        
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    config.readString(MailConfig.SMTP_LOGIN), 
                    config.readString(MailConfig.SMTP_PASSWORD));
            }
        });
        
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(this.config.readString(MailConfig.SMTP_FROM)));
        msg.setRecipients(RecipientType.TO, this.config.getRecipients());
        msg.setSubject(subject);
        msg.setText(message);
        
        logger.trace("Sending mail...");
        Transport.send(msg);
        logger.trace("Mail sent");
    }
}
