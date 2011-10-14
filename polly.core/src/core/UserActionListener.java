package core;

import java.util.Date;

import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.model.User;


public class UserActionListener implements MessageListener {

    private UserManager userManager;
    private PersistenceManager persistence;
    private FormatManager formatter;
    
    
    public UserActionListener(MyPolly myPolly) {
        this.userManager = myPolly.users();
        this.persistence = myPolly.persistence();
        this.formatter = myPolly.formatting();
    }
    
    
    
    @Override
    public void publicMessage(MessageEvent e) {
        this.updateLastAction(e);
    }

    @Override
    public void privateMessage(MessageEvent e) {
        this.updateLastAction(e);
    }

    @Override
    public void actionMessage(MessageEvent e) {
        this.updateLastAction(e);
    }
    
    
    private synchronized void updateLastAction(MessageEvent e) {
        User u = this.userManager.getUser(e.getUser());

        String s = "Letzte Nachricht: " + e.getMessage() + " (Uhrzeit: " + 
            this.formatter.formatDate(new Date());
        if (!e.inQuery()) {
            s += ", Channel: " + e.getChannel();
        }
        s += ")";
        
        try {
            this.persistence.writeLock();
            this.persistence.startTransaction();
            u.setAttribute("SEEN", s);
            this.persistence.commitTransaction();
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            this.persistence.writeUnlock();
        }
        
    }

}