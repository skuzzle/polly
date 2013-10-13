package core;

import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.MailManager;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.exceptions.EMailException;
import de.skuzzle.polly.sdk.time.Milliseconds;
import de.skuzzle.polly.tools.concurrent.RunLater;


public class MailRunLater extends RunLater implements MessageListener {

    private final static long DELAY = Milliseconds.fromSeconds(30);
    
    private final User user;
    private final IrcManager irc;
    private final MailManager mails;
    private final String subject;
    private final String message;
    private final String receiver;
    
    
    public MailRunLater(User user, IrcManager irc, MailManager mails, String subject, 
            String message, String receiver) {
        super("MAIL_DELAY_" + user.getName(), DELAY); //$NON-NLS-1$
        this.user = user;
        this.irc = irc;
        this.mails = mails;;
        this.subject = subject;
        this.message = message;
        this.receiver = receiver;
        irc.addMessageListener(this);
    }
    
    

    @Override
    public void run() {
        try {
            this.mails.sendMail(this.receiver, this.subject, this.message);
        } catch (EMailException e) {
            e.printStackTrace();
        }
    }
    
    
    
    @Override
    public void finished() {
        this.irc.removeMessageListener(this);
    }
    
    
    
    @Override
    public void interrupted() {
        this.irc.removeMessageListener(this);
    }
    
    
    
    private void checkActivity(MessageEvent e) {
        if (e.getUser().getNickName().equals(this.user.getCurrentNickName())) {
            this.stop();
        }
    }
    


    @Override
    public void publicMessage(MessageEvent e) {
        this.checkActivity(e);
    }



    @Override
    public void privateMessage(MessageEvent e) {
        this.checkActivity(e);
    }



    @Override
    public void actionMessage(MessageEvent e) {
        this.checkActivity(e);
    }



    @Override
    public void noticeMessage(MessageEvent e) {
        this.checkActivity(e);
    }
}
