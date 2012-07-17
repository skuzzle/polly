package core;

import java.util.Date;
import java.util.TimerTask;

import de.skuzzle.polly.sdk.Disposable;
import de.skuzzle.polly.sdk.eventlistener.IrcUser;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.EMailException;
import de.skuzzle.polly.sdk.model.User;

import entities.RemindEntity;


public interface RemindManager extends Disposable {
    
    
    
    public static class RemindTask extends TimerTask {

        private RemindManager remindManager;
        private RemindEntity remind;
        
        
        public RemindTask(RemindManager remindManager, RemindEntity remind) {
            this.remindManager = remindManager;
            this.remind = remind;
        }
        
        
        
        @Override
        public void run() {
            try {
                this.remindManager.deliverRemind(this.remind, true);
            } catch (Exception e) {
                // todo exception handling
                e.printStackTrace();
            }
        }
        
    }
    
    
    
    public static class SleepTask extends TimerTask {

        private String forUser;
        private RemindManager remindManager;
        
        
        public SleepTask(RemindManager remindManager, String forUser) {
            this.remindManager = remindManager;
            this.forUser = forUser;
        }
        
        
        @Override
        public void run() {
            this.remindManager.cancelSleep(this.forUser);
        }
        
    }
    
    
    public abstract RemindDBWrapper getDatabaseWrapper();

    public abstract void addRemind(RemindEntity remind, boolean schedule) 
            throws DatabaseException;
    
    public abstract void deleteRemind(int id) throws DatabaseException;
    
    public abstract void deleteRemind(RemindEntity remind) throws DatabaseException;
    
    public abstract void deleteRemind(User executor, int id) 
            throws CommandException, DatabaseException;
    
    public abstract void deliverRemind(RemindEntity remind, boolean ignoreIdleStatus) 
            throws DatabaseException, EMailException;
    
    public abstract void deliverLater(RemindEntity remind, User forUser, boolean wasIdle) 
            throws DatabaseException, EMailException;
    
    public abstract void deliverNowIrc(RemindEntity remind, User forUser);
    
    public abstract void deliverNowMail(RemindEntity remind, User forUser) 
            throws DatabaseException, EMailException;
    
    public abstract void scheduleRemind(RemindEntity remind);
    
    public abstract void scheduleRemind(RemindEntity remind, Date dueDate);
    
    public abstract void cancelScheduledRemind(RemindEntity remind);
    
    public abstract void cancelScheduledRemind(int id);
    
    public abstract void putToSleep(RemindEntity remind, User forUser);
    
    public abstract RemindEntity cancelSleep(RemindEntity remind);
    
    public abstract RemindEntity cancelSleep(String forUser);
    
    public abstract void snooze(User executor, Date dueDate) 
            throws CommandException, DatabaseException;
    
    public abstract RemindEntity toggleIsMail(User executor, int id) 
            throws DatabaseException, CommandException;
    
    public abstract User getUser(String nickName);
    
    public abstract RemindFormatter getFormat(User user);
    
    public abstract boolean isIdle(User user);
    
    public abstract boolean isOnActionAvailable(String forUser);
    
    public abstract void modifyRemind(User executor, int id, final Date dueDate, 
        final String msg) throws CommandException, DatabaseException;
    
    public abstract boolean canEdit(User user, RemindEntity remind);
    
    public abstract void checkRemind(User user, RemindEntity remind, int id) 
            throws CommandException;
    
    public abstract void traceNickChange(IrcUser oldUser, IrcUser newUser);
    
    public abstract void rescheduleAll();
    
    public abstract boolean isStale(String forUser);
}