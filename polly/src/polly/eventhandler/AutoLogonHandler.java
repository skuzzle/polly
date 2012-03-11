package polly.eventhandler;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;


import polly.core.DefaultUserAttributes;
import polly.core.conversations.ConversationManagerImpl;
import polly.core.irc.IrcManagerImpl;
import polly.core.users.UserManagerImpl;
import polly.util.concurrent.ThreadFactoryBuilder;
import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.Conversation;
import de.skuzzle.polly.sdk.eventlistener.NickChangeEvent;
import de.skuzzle.polly.sdk.eventlistener.NickChangeListener;
import de.skuzzle.polly.sdk.eventlistener.SpotEvent;
import de.skuzzle.polly.sdk.eventlistener.UserEvent;
import de.skuzzle.polly.sdk.eventlistener.UserListener;
import de.skuzzle.polly.sdk.eventlistener.UserSpottedListener;
import de.skuzzle.polly.sdk.exceptions.AlreadySignedOnException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;
import de.skuzzle.polly.sdk.model.User;


public class AutoLogonHandler extends AbstractDisposable
        implements UserSpottedListener, NickChangeListener, UserListener {
    
    private final static User NICKSERV = new polly.data.User("NickServ", "", 0);
    static {
        NICKSERV.setCurrentNickName("NickServ");
    }
    
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
            
            synchronized (scheduledLogons) {
                if (scheduledLogons.containsKey(this.forUser)) {
                    Conversation c = null;
                    try {
                        // removing the entry happens in the event handler
                        // AutoLogonLogoffHandler.this.scheduledLogons.remove(this.forUser);
                        
                        // ISSUE 0000091: check nickserv if user is registered and 
                        //                logged in
                        c = convManager.create(ircManager, NICKSERV, 
                                               ircManager.getNickname(), 5000);
                        ircManager.sendRawCommand("NICKSERV STATUS " + this.forUser);
                        String reply = c.readStringLine();
                        
                        if (reply.equalsIgnoreCase("status " + this.forUser + " 3")) {
                            userManager.logonWithoutPassword(this.forUser);
                        } else {
                            logger.warn("User '" + this.forUser + "' could not be " +
                            		"logged on automatically. NickServ replied: '" + 
                            		reply + "'");
                        }
                    } catch (InterruptedException e) {
                        logger.error("Timeout while waiting for nickserv reply", e);
                    } catch (UnknownUserException e) {
                        logger.warn("Error while autologon", e);
                    } catch (AlreadySignedOnException e) {
                        logger.warn("Error while autologon", e);
                    } catch (Exception e) {
                        logger.error("Error while autologon", e);
                    } finally {
                        if (c != null) {
                            c.close();
                        }
                    }
                }
            }
        }
    }
    

    private static Logger logger = Logger.getLogger(AutoLogonHandler.class.getName());
    
 
    private IrcManagerImpl ircManager;
    private ConversationManagerImpl convManager;
    private UserManagerImpl userManager;
    private ScheduledExecutorService autoLogonExecutor;
    private Map<String, AutoLogonRunnable> scheduledLogons;
    private int autoLoginTime;
    
    public AutoLogonHandler(IrcManagerImpl ircManager, 
            ConversationManagerImpl convManager, UserManagerImpl userManager, 
            int autoLoginTime) {
        this.ircManager = ircManager;
        this.userManager = userManager;
        this.autoLogonExecutor = Executors.newScheduledThreadPool(4, 
                new ThreadFactoryBuilder("LOGON"));
        this.scheduledLogons = new HashMap<String, AutoLogonRunnable>();
        this.autoLoginTime = autoLoginTime;
        this.convManager = convManager;
    }



    @Override
    public void userSpotted(SpotEvent e) {
        this.scheduleAutoLogon(e.getUser().getNickName());
    }

    
    
    @Override
    public void nickChanged(NickChangeEvent e) {
        /*
         * If there is a auto logon scheduled for the old nickname, it will be 
         * canceled. If there is a registered user with the new nickname that is 
         * currently not logged on, a new auto logon is scheduled for that user. 
         */
        synchronized (this.scheduledLogons) {
            AutoLogonRunnable alr = this.scheduledLogons.get(e.getOldUser().getNickName());
            
            if (alr != null) {
                alr.cancel();
                this.scheduledLogons.remove(e.getOldUser().getNickName());
                logger.debug("Auto logon for " + e.getOldUser() + " canceled");
            }
            
            User u = this.userManager.getUser(e.getNewUser().getNickName());
            if (u != null && !this.userManager.isSignedOn(u)) {
                this.scheduleAutoLogon(e.getNewUser().getNickName());
            }
        }
    }

    
    
    private void scheduleAutoLogon(String forUser) {
        synchronized (this.scheduledLogons) {
            if (this.userExists(forUser)) {
                AutoLogonRunnable runMe = new AutoLogonRunnable(forUser);
                this.scheduledLogons.put(forUser, runMe);
                this.autoLogonExecutor.schedule(runMe, this.autoLoginTime, 
                        TimeUnit.MILLISECONDS);
                logger.debug("Auto logon for " + forUser + " scheduled");
            }
        }
    }
    
    
    
    private boolean userExists(String name) {
        User user = this.userManager.getUser(name);
        return user != null && 
            user.getAttribute(DefaultUserAttributes.AUTO_LOGON).equalsIgnoreCase("true");
    }
    
    

    @Override
    public void userLost(SpotEvent ignore) {}



    @Override
    public void userSignedOn(UserEvent e) {
        synchronized (this.scheduledLogons) {
            AutoLogonRunnable alr = this.scheduledLogons.get(e.getUser().getName());
            if (alr != null) {
                logger.trace("Removing scheduled auto logon for user " + 
                    e.getUser().getName());
                alr.cancel();
                this.scheduledLogons.remove(e.getUser().getName());
            }
        }
    }



    @Override
    public void userSignedOff(UserEvent ignore) {}



    @Override
    protected void actualDispose() throws DisposingException {
        this.autoLogonExecutor.shutdown();
    }
}
