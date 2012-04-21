package core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import polly.core.MyPlugin;

import de.skuzzle.polly.sdk.MailManager;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.exceptions.EMailException;
import de.skuzzle.polly.sdk.model.User;


public class ForwardHighlightHandler implements MessageListener {
    
    private final static String SUBJECT = "[POLLY Highlight Forwarder] Highlight in %s";
    
    private final static String MESSAGE = "Hi %s,\n\nDu wurdest im Channel %s " +
    		"von %s gehighlighted. Nachricht:\n%s\n\n Bye\nPolly";
    
    
    public final static long MAIL_DELAY = 30000; // 30 seconds
    
    private MailManager mailManager;
    private UserManager userManager;
    private Map<String, Long> timestamps;
    
    
    
    public ForwardHighlightHandler(MailManager mailManager, UserManager userManager) {
        this.mailManager = mailManager;
        this.userManager = userManager;
        this.timestamps = new HashMap<String, Long>();
    }
    
    
    
    public void publicMessage(MessageEvent e) {
        this.forwardHighlight(e);
    }

    

    @Override
    public void actionMessage(MessageEvent e) {
        this.forwardHighlight(e);
    }
    
    
    
    private void forwardHighlight(MessageEvent e) {
        Collection<User> allUsers = this.userManager.getRegisteredUsers();
        
        for (User user : allUsers) {
            
            String mail = user.getAttribute("EMAIL");
            
            // forward if user is idle, wants forward and has a mail address set
            boolean fwd = user.isIdle() &&
                user.getAttribute(MyPlugin.FORWARD_HIGHLIGHTS).equals("true") &&
                !mail.equals("none");
            // if user is offline, the nick to check is the username, otherwise the 
            // current nickname
            String nick = user.getCurrentNickName() == null 
                ? user.getName() 
                : user.getCurrentNickName();
                
            // ignore self highlighting
            if (e.getUser().getNickName().equals(nick)) {
                continue;
            }
            boolean hl = e.getMessage().toLowerCase().indexOf(nick.toLowerCase()) != -1;

            if (fwd && hl && this.canSend(mail)) {
                String subject = String.format(SUBJECT, e.getChannel());
                String message = String.format(MESSAGE, user.getName(), e.getChannel(), 
                    e.getUser(), e.getMessage());
                
                try {
                    this.mailManager.sendMail(mail, subject, message);
                } catch (EMailException e1) {
                    e1.printStackTrace();
                }
                
            }
        }
    }
    
    
    
    private boolean canSend(String recipient) {
        synchronized (this.timestamps) {
            Long ts = this.timestamps.get(recipient);
            if (ts == null) {
                this.timestamps.put(recipient, System.currentTimeMillis());
                return true;
            }
            long diff = System.currentTimeMillis() - ts;
            if (diff < MAIL_DELAY) {
                return false;
            } else {
                this.timestamps.put(recipient, System.currentTimeMillis());
                return true;
            }
        }
    }

    

    @Override
    public void noticeMessage(MessageEvent ignore) {}
    
    
    
    @Override
    public void privateMessage(MessageEvent ignore) {}

}
