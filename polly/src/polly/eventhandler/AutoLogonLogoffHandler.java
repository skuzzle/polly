package polly.eventhandler;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import polly.core.IrcManagerImpl;
import polly.core.UserManagerImpl;
import de.skuzzle.polly.sdk.eventlistener.NickChangeEvent;
import de.skuzzle.polly.sdk.eventlistener.NickChangeListener;
import de.skuzzle.polly.sdk.eventlistener.SpotEvent;
import de.skuzzle.polly.sdk.eventlistener.UserSpottedListener;
import de.skuzzle.polly.sdk.exceptions.AlreadySignedOnException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;
import de.skuzzle.polly.sdk.model.User;

// TODO:
// Implement auto logon for users who have already been spotted but not with their
// registered nick. Therefore auto login must be triggered when changing nick to a 
// registered username and this user must nor currently be logged in
public class AutoLogonLogoffHandler implements UserSpottedListener, NickChangeListener {
    
    
    private class AutoLogonRunnable implements Runnable {
        
        private String forUser;
        private boolean canceled;
        
        
        public AutoLogonRunnable(String forUser) {
            this.forUser = forUser;
        }
        
        
        public void cancel() {
            this.canceled = true;
        }
        
        
        @Override
        public void run() {
            if (this.canceled) {
                return;
            }
            
            synchronized (AutoLogonLogoffHandler.this.scheduledLogons) {
                if (AutoLogonLogoffHandler.this.scheduledLogons.containsKey(this.forUser)) {
                    try {
                        AutoLogonLogoffHandler.this.scheduledLogons.remove(this.forUser);
                        AutoLogonLogoffHandler.this.userManager.logonWithoutPassword(this.forUser);
                    } catch (UnknownUserException e) {
                        logger.warn("Error while autologon", e);
                    } catch (AlreadySignedOnException e) {
                        logger.warn("Error while autologon", e);
                    }
                }
            }
        }
    }

    private static Logger logger = Logger.getLogger(AutoLogonLogoffHandler.class.getName());
    
    private IrcManagerImpl ircManager;
    private UserManagerImpl userManager;
    private ScheduledExecutorService autoLogonExecutor;
    private Map<String, AutoLogonRunnable> scheduledLogons;
    
    
    public AutoLogonLogoffHandler(IrcManagerImpl ircManager, UserManagerImpl userManager) {
        this.ircManager = ircManager;
        this.userManager = userManager;
        this.autoLogonExecutor = Executors.newScheduledThreadPool(4);
        this.scheduledLogons = new HashMap<String, AutoLogonRunnable>();
    }



    @Override
    public void userSpotted(SpotEvent e) {
        this.scheduleAutoLogon(e.getUser().getNickName());
    }

    
    
    @Override
    public void nickChanged(NickChangeEvent e) {
        synchronized (this.scheduledLogons) {
            AutoLogonRunnable alr = this.scheduledLogons.get(e.getOldUser().getNickName());
            
            if (alr != null) {
                alr.cancel();
                this.scheduledLogons.remove(e.getOldUser().getNickName());

                if (this.userExists(e.getNewUser().getNickName())) {
                    this.scheduleAutoLogon(e.getNewUser().getNickName());
                }
            }
        }
    }

    
    
    private void scheduleAutoLogon(String forUser) {
        synchronized (this.scheduledLogons) {
            if (this.userExists(forUser)) {
                AutoLogonRunnable runMe = new AutoLogonRunnable(forUser);
                this.scheduledLogons.put(forUser, runMe);
                this.autoLogonExecutor.schedule(runMe, 60, TimeUnit.SECONDS);
            }
        }
    }
    
    
    
    private boolean userExists(String name) {
        User user = this.userManager.getUser(name);
        return user != null;
    }
    
    

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
