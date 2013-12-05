package de.skuzzle.polly.core.eventhandler;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import de.skuzzle.polly.core.internal.DefaultUserAttributesProvider;
import de.skuzzle.polly.core.internal.irc.IrcManagerImpl;
import de.skuzzle.polly.core.internal.users.UserManagerImpl;
import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.Types.BooleanType;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.eventlistener.ConnectionEvent;
import de.skuzzle.polly.sdk.eventlistener.ConnectionListener;
import de.skuzzle.polly.sdk.eventlistener.MessageAdapter;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.NickChangeEvent;
import de.skuzzle.polly.sdk.eventlistener.NickChangeListener;
import de.skuzzle.polly.sdk.eventlistener.SpotEvent;
import de.skuzzle.polly.sdk.eventlistener.UserEvent;
import de.skuzzle.polly.sdk.eventlistener.UserListener;
import de.skuzzle.polly.sdk.eventlistener.UserSpottedListener;
import de.skuzzle.polly.sdk.exceptions.AlreadySignedOnException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;
import de.skuzzle.polly.tools.concurrent.ThreadFactoryBuilder;



public class AutoLogonHandler extends AbstractDisposable
        implements UserSpottedListener, NickChangeListener, UserListener, 
                   ConnectionListener {

    
    private class AutoLogonRunnable implements Runnable {
        
        private final String forUser;
        private volatile boolean canceled;
        
        
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
            // ISSUE 0000091: request status for the user from nickserv
            requestAuthStatus(this.forUser);
        }
    }
    
    
    
    private MessageAdapter autoSignOnHandler = new MessageAdapter() {
        @Override
        public void noticeMessage(MessageEvent e) {
            if (!e.getUser().getNickName().equalsIgnoreCase("nickserv")) { //$NON-NLS-1$
                return;
            }
            final String[] parts = e.getMessage().split(" "); //$NON-NLS-1$
            if (parts.length != 3 || 
                    !parts[0].equalsIgnoreCase("status") ||  //$NON-NLS-1$
                    !parts[2].equals("3")) { //$NON-NLS-1$
                return;
            }
            final String forUser = parts[1];
            
            try {
                userManager.logonWithoutPassword(forUser);
            } catch (AlreadySignedOnException e1) {
                logger.trace("User logged in while waiting for auto logon"); //$NON-NLS-1$
            } catch (UnknownUserException e1) {
                logger.error("Error while auto logon. User '" + forUser +  //$NON-NLS-1$
                    "' unknown", e1); //$NON-NLS-1$
            }
        }
    };
    

    private static Logger logger = Logger.getLogger(AutoLogonHandler.class.getName());
    
 
    private final IrcManagerImpl ircManager;
    private final UserManagerImpl userManager;
    private final ScheduledExecutorService autoLogonExecutor;
    private final Map<String, AutoLogonRunnable> scheduledLogons;
    private final int autoLoginDelay;
    
    
    
    public AutoLogonHandler(IrcManagerImpl ircManager, UserManagerImpl userManager, 
            int autoLoginDelay) {
        
        ircManager.addMessageListener(this.autoSignOnHandler);
        this.ircManager = ircManager;
        this.userManager = userManager;
        this.autoLogonExecutor = Executors.newScheduledThreadPool(1, 
                new ThreadFactoryBuilder("LOGON")); //$NON-NLS-1$
        this.scheduledLogons = new HashMap<String, AutoLogonRunnable>();
        this.autoLoginDelay = autoLoginDelay;
    }



    @Override
    public void userSpotted(SpotEvent e) {
        final String forUser = e.getUser().getNickName();
        if (this.userExists(forUser)) {
            // try instant login if user just spotted 
            this.ircManager.sendRawCommand("NICKSERV STATUS " + forUser); //$NON-NLS-1$
        }
        this.scheduleAutoLogon(forUser);
    }

    
    
    @Override
    public void nickChanged(NickChangeEvent e) {
        /*
         * If there is a auto logon scheduled for the old nickname, it will be 
         * canceled. If there is a registered user with the new nickname that is 
         * currently not logged on, a new auto logon is scheduled for that user. 
         */
        synchronized (this.scheduledLogons) {
            final AutoLogonRunnable alr = this.scheduledLogons.get(
                    e.getOldUser().getNickName());
            
            if (alr != null) {
                alr.cancel();
                this.scheduledLogons.remove(e.getOldUser().getNickName());
                logger.debug("Auto logon for " + e.getOldUser() + " canceled"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            
            final User u = this.userManager.getUser(e.getNewUser().getNickName());
            if (u != null && !this.userManager.isSignedOn(u)) {
                this.scheduleAutoLogon(e.getNewUser().getNickName());
            }
        }
    }
    
    
    
    private void requestAuthStatus(String forUser) {
        synchronized (this.scheduledLogons) {
            if (this.scheduledLogons.containsKey(forUser)) {
                this.scheduledLogons.remove(forUser);
                this.ircManager.sendRawCommand("NICKSERV STATUS " + forUser); //$NON-NLS-1$
            }
        }
    }
    
    
    
    private void scheduleAutoLogon(String forUser) {
        synchronized (this.scheduledLogons) {
            if (this.scheduledLogons.containsKey(forUser)) {
                return;
            }
            if (this.userExists(forUser)) {
                final AutoLogonRunnable runMe = new AutoLogonRunnable(forUser);
                this.scheduledLogons.put(forUser, runMe);
                this.autoLogonExecutor.schedule(runMe, this.autoLoginDelay, 
                        TimeUnit.MILLISECONDS);
                logger.debug("Auto logon for " + forUser + " scheduled"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }
    
    
    
    private boolean userExists(String name) {
       final  User user = this.userManager.getUser(name);
        if (user == null) {
            return false;
        }
        final BooleanType bool = (BooleanType) 
            user.getAttribute(DefaultUserAttributesProvider.AUTO_LOGON);
        return bool.getValue();
    }
    
    

    @Override
    public void userSignedOn(UserEvent e) {
        synchronized (this.scheduledLogons) {
            final AutoLogonRunnable alr = this.scheduledLogons.get(e.getUser().getName());
            if (alr != null) {
                logger.trace("Removing scheduled auto logon for user " +  //$NON-NLS-1$
                    e.getUser().getName());
                alr.cancel();
                this.scheduledLogons.remove(e.getUser().getName());
            }
        }
    }



    @Override
    protected void actualDispose() throws DisposingException {
        this.autoLogonExecutor.shutdown();
        try {
            this.autoLogonExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignore) {}
    }
    
    
    
    @Override
    public void userSignedOff(UserEvent ignore) {}

    @Override
    public void userLost(SpotEvent ignore) {}

    @Override
    public void ircConnectionEstablished(ConnectionEvent e) {}

    @Override
    public void ircConnectionLost(ConnectionEvent e) {}
}
