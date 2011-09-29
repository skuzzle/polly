package polly.eventhandler;

import org.apache.log4j.Logger;

import polly.core.IrcManagerImpl;
import polly.core.UserManagerImpl;
import de.skuzzle.polly.sdk.eventlistener.SpotEvent;
import de.skuzzle.polly.sdk.eventlistener.UserSpottedListener;

public class IsGoneHandler implements UserSpottedListener {

    private static Logger logger = Logger.getLogger(IsGoneHandler.class.getName());
    
    private IrcManagerImpl ircManager;
    private UserManagerImpl userManager;
    
    public IsGoneHandler(IrcManagerImpl ircManager, UserManagerImpl userManager) {
        this.ircManager = ircManager;
        this.userManager = userManager;
    }



    @Override
    public void userSpotted(SpotEvent e) {}



    @Override
    public synchronized void userLost(SpotEvent e) {
        if (this.userManager.isSignedOn(e.getUser())) {
            logger.warn("Auto logoff for user: " + e.getUser());
            if (e.getType() != SpotEvent.USER_QUIT) {
                this.ircManager.sendMessage(e.getUser().getNickName(), 
                    "Du wurdest automatisch ausgeloggt weil du den letzten " +
                    "gemeinsamen Channel verlassen hast.");
            }
            this.userManager.logoff(e.getUser(), true);
        }
    }
}
