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
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
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
    
    
    
    private final MessageListener autoSignOnHandler = new MessageListener() {
        private void process(MessageEvent e) {
            try {
                provider.processMessageEvent(e, userManager);
            } catch (AlreadySignedOnException e1) {
                logger.trace("User logged in while waiting for auto logon"); //$NON-NLS-1$
            } catch (UnknownUserException e1) {
                logger.error("Error while auto logon. User '" + e1.getMessage() +  //$NON-NLS-1$
                    "' unknown", e1); //$NON-NLS-1$
            }
        }
        @Override
        public void noticeMessage(MessageEvent e) {
            this.process(e);
        }
        @Override
        public void publicMessage(MessageEvent e) {
            this.process(e);
        }

        @Override
        public void privateMessage(MessageEvent e) {
            this.process(e);
        }

        @Override
        public void actionMessage(MessageEvent e) {
            this.process(e);
        }
    };
    
    

    private static Logger logger = Logger.getLogger(AutoLogonHandler.class.getName());
    
 
    private final IrcManagerImpl ircManager;
    private final UserManagerImpl userManager;
    private final ScheduledExecutorService autoLogonExecutor;
    private final Map<String, AutoLogonRunnable> scheduledLogons;
    private final int autoLoginDelay;
    private final AutoLoginProvider provider;
    
    
    
    public AutoLogonHandler(IrcManagerImpl ircManager, UserManagerImpl userManager, 
            AutoLoginProvider provider, int autoLoginDelay) {
        
        ircManager.addMessageListener(this.autoSignOnHandler);
        this.provider = provider;
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
            this.provider.requestAuthentification(forUser, this.ircManager);
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
                logger.debug("Auto logon for " + e.getOldUser() + " canceled");   //$NON-NLS-1$//$NON-NLS-2$
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
                this.provider.requestAuthentification(forUser, this.ircManager);
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
