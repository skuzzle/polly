package polly.eventhandler;

import org.apache.log4j.Logger;

import polly.core.IrcManagerImpl;
import polly.core.UserManagerImpl;
import de.skuzzle.polly.sdk.eventlistener.ChannelEvent;
import de.skuzzle.polly.sdk.eventlistener.IrcUser;
import de.skuzzle.polly.sdk.eventlistener.JoinPartListener;
import de.skuzzle.polly.sdk.eventlistener.QuitEvent;
import de.skuzzle.polly.sdk.eventlistener.QuitListener;

public class IsGoneHandler implements QuitListener, JoinPartListener {

    private static Logger logger = Logger.getLogger(IsGoneHandler.class.getName());
    
    private IrcManagerImpl ircManager;
    private UserManagerImpl userManager;
    
    public IsGoneHandler(IrcManagerImpl ircManager, UserManagerImpl userManager) {
        this.ircManager = ircManager;
        this.userManager = userManager;
    }
    
    
    
    @Override
    public void channelJoined(ChannelEvent e) {}

    
    
    @Override
    public void channelParted(ChannelEvent e) {
        this.traceGone(e.getUser(), false);
    }

    
    
    @Override
    public void quited(QuitEvent e) {
        this.traceGone(e.getUser(), true);
    }
    
    
    
    private synchronized void traceGone(IrcUser user, boolean quit) {
        if (!this.ircManager.isOnline(user.getNickName()) && 
                this.userManager.isSignedOn(user)) {
            
            logger.warn("Auto logoff for user: " + user);
            if (!quit) {
                this.ircManager.sendMessage(user.getNickName(), 
                    "Du wurdest automatisch ausgeloggt.");
            }
            this.userManager.logoff(user);
        }
    }
}
