package polly.eventhandler;

import polly.core.users.UserManagerImpl;
import de.skuzzle.polly.sdk.eventlistener.ConnectionEvent;
import de.skuzzle.polly.sdk.eventlistener.ConnectionListener;


public class IrcConnectionLostListener implements ConnectionListener {

    private UserManagerImpl userManager;
    
    
    public IrcConnectionLostListener(UserManagerImpl userManager) {
        this.userManager = userManager;
    }
    
    @Override
    public void ircConnectionEstablished(ConnectionEvent e) {}

    
    @Override
    public void ircConnectionLost(ConnectionEvent e) {
        this.userManager.logoffAll();
    }

}
