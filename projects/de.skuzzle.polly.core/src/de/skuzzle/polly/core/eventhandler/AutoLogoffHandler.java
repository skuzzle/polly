package de.skuzzle.polly.core.eventhandler;

import org.apache.log4j.Logger;


import de.skuzzle.polly.core.internal.irc.IrcManagerImpl;
import de.skuzzle.polly.core.internal.users.UserManagerImpl;
import de.skuzzle.polly.sdk.eventlistener.ConnectionEvent;
import de.skuzzle.polly.sdk.eventlistener.ConnectionListener;
import de.skuzzle.polly.sdk.eventlistener.SpotEvent;
import de.skuzzle.polly.sdk.eventlistener.UserSpottedListener;


public class AutoLogoffHandler implements UserSpottedListener, ConnectionListener {
    
    private final static Logger logger = Logger.getLogger(
            AutoLogoffHandler.class.getName());

    
    private UserManagerImpl userManager;
    private IrcManagerImpl ircManager;
    
    
    
    public AutoLogoffHandler(UserManagerImpl userManager, IrcManagerImpl ircManager) {
        this.userManager = userManager;
        this.ircManager = ircManager;
    }
    

    
    @Override
    public void userLost(SpotEvent e) {
        if (this.userManager.isSignedOn(e.getUser())) {
            logger.warn("Auto logoff for user: " + e.getUser()); //$NON-NLS-1$
            if (e.getType() != SpotEvent.USER_QUIT) {
                this.ircManager.sendMessage(e.getUser().getNickName(), 
                    MSG.autoLogoff, this);
            }
            this.userManager.logoff(e.getUser(), true);
        }
    }

    
    
    @Override
    public void userSpotted(SpotEvent ignore) {}



    @Override
    public void ircConnectionEstablished(ConnectionEvent e) {
    }



    @Override
    public void ircConnectionLost(ConnectionEvent e) {
        this.userManager.logoffAll();
    }
}
