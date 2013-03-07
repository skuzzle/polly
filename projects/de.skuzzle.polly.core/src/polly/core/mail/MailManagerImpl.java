package polly.core.mail;

import javax.mail.MessagingException;

import polly.core.mail.senders.MailSender;

import de.skuzzle.polly.sdk.MailManager;
import de.skuzzle.polly.sdk.exceptions.EMailException;


public class MailManagerImpl implements MailManager {

    public final static long MAIL_DELAY = 30000; // 30 seconds
    
    private MailSender sender;
    
    
    public MailManagerImpl(MailSender sender) {
        this.sender = sender;
    }
    
    
    
    @Override
    public void sendMail(String recipient, String subject, String message)
            throws EMailException {
        try {
            this.sender.sendMail(recipient, subject, message);
        } catch (MessagingException e) {
            throw new EMailException(e.getMessage(), e);
        }
    }

}