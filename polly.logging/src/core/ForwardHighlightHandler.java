package core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polly.logging.MyPlugin;


import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.MailManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.EMailException;
import de.skuzzle.polly.sdk.model.User;
import entities.LogEntry;


public class ForwardHighlightHandler implements MessageListener {
    
    private final static String SUBJECT = "[POLLY Highlight Forwarder] Highlight in %s";
    
    private final static String MESSAGE = "Hi %s,\n\nDu wurdest im Channel %s " +
    		"von %s gehighlighted. Nachricht:\n%s\n\n " +
    		"Channellog:\n%s\n\n" +
    		"Bye\nPolly";
    
    
    public final static long MAIL_DELAY = 30000; // 30 seconds
    
    private MailManager mailManager;
    private UserManager userManager;
    private Map<String, Long> timestamps;
    private PollyLoggingManager logManager;
    private LogFormatter logFormatter;
    private FormatManager formatManager;
    
    
    
    public ForwardHighlightHandler(MyPolly myPolly, PollyLoggingManager logManager) {
        this.mailManager = myPolly.mails();
        this.userManager = myPolly.users();
        this.formatManager = myPolly.formatting();
        this.timestamps = new HashMap<String, Long>();
        this.logManager = logManager;
        this.logFormatter = new DefaultLogFormatter();
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
            boolean hl = e.getMessage().toLowerCase().contains(nick.toLowerCase());

            if (fwd && hl && this.canSend(mail)) {
                try {
                    List<LogEntry> prefiltered = this.logManager.preFilterChannel(
                        e.getChannel(), 10);
                    
                    Collections.reverse(prefiltered);
                    
                    String logs = this.formatList(prefiltered);
                    String subject = String.format(SUBJECT, e.getChannel());
                    String message = String.format(MESSAGE, user.getName(), e.getChannel(), 
                        e.getUser(), e.getMessage(), logs);
                
                    this.mailManager.sendMail(mail, subject, message);
                } catch (DatabaseException e1) {
                    e1.printStackTrace();
                } catch (EMailException e1) {
                    e1.printStackTrace();
                }
                
            }
        }
    }
    
    
    
    private String formatList(List<LogEntry> logs) {
        StringBuilder b = new StringBuilder();
        for (LogEntry logEntry : logs) {
            b.append(this.logFormatter.formatLog(logEntry, this.formatManager));
            b.append(System.lineSeparator());
        }
        return b.toString();
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