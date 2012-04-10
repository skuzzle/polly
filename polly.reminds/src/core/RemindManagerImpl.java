package core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;


import polly.reminds.MyPlugin;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.MailManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.WriteAction;
import de.skuzzle.polly.sdk.eventlistener.IrcUser;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.EMailException;
import de.skuzzle.polly.sdk.model.User;
import entities.RemindEntity;



public class RemindManagerImpl extends AbstractDisposable implements RemindManager {
    
    private final static RemindFormatter MAIL_FORMAT = new MailRemindFormatter();
    
    private final static String SUBJECT = "[Reminder] Erinnerung um %s";
    
    public final static RemindFormatter DEFAULT_FORMAT = 
        PatternRemindFormatter.forPattern(MyPlugin.REMIND_FORMAT_VALUE);
    
    // XXX: special case for clum
    private final static RemindFormatter heidiFormat = new HeidiRemindFormatter();
    
    
    private MailManager mails;
    private IrcManager irc;
    private PersistenceManager persistence;
    private UserManager userManager;
    private Timer remindScheduler;
    private Map<Integer, RemindTask> scheduledReminds;
    private Map<String, RemindFormatter> specialFormats;
    private Map<String, RemindEntity> sleeping;
    private OnActionSet onActionSet;
    private FormatManager formatter;
    private RemindDBWrapper dbWrapper;
    
    
    
    public RemindManagerImpl(MyPolly myPolly) {
        this.mails = myPolly.mails();
        this.irc = myPolly.irc();
        this.persistence = myPolly.persistence();
        this.userManager = myPolly.users();
        this.formatter = myPolly.formatting();
        this.dbWrapper = new RemindDBWrapperImpl(myPolly.persistence());
        this.remindScheduler = new Timer("REMIND_SCHEDULER", true);
        this.scheduledReminds = new HashMap<Integer, RemindManager.RemindTask>();
        this.specialFormats = new HashMap<String, RemindFormatter>();
        this.sleeping = new HashMap<String, RemindEntity>();
        this.onActionSet = new OnActionSet();
        
        // XXX: special case for clum:
        this.specialFormats.put("clum", heidiFormat);
    }
    
    
    
    @Override
    public RemindDBWrapper getDatabaseWrapper() {
        return this.dbWrapper;
    }
    
    
    
    @Override
    public void addRemind(final RemindEntity remind, boolean schedule) 
                throws DatabaseException {
        
        this.dbWrapper.addRemind(remind);
        
        if (schedule) {
            this.scheduleRemind(remind);
        }
        
        if (remind.isOnAction()) {
            this.onActionSet.put(remind.getForUser());
        }
    }
    
    

    @Override
    public void deleteRemind(int id) throws DatabaseException {
        RemindEntity remind = this.dbWrapper.getRemind(id);
        this.deleteRemind(remind);
    }

    
    
    @Override
    public void deleteRemind(RemindEntity remind) throws DatabaseException {
        if (remind != null) {
            this.cancelScheduledRemind(remind.getId());
            this.dbWrapper.deleteRemind(remind);
        }
    }
    
    
    
    @Override
    public void deleteRemind(User executor, int id) 
            throws CommandException, DatabaseException {
        RemindEntity remind = this.dbWrapper.getRemind(id);
        this.checkRemind(executor, remind, id);
        this.deleteRemind(remind);
    }
    
    
    
    @Override
    public void deliverRemind(RemindEntity remind, boolean checkIdleStatus) 
            throws DatabaseException, EMailException {
        User forUser = this.getUser(remind.getForUser());
        
        try {
            if (remind.isMail()) {
                this.deliverNowMail(remind, forUser);
            } else {
            
                boolean idle = this.isIdle(forUser) && checkIdleStatus;
                boolean online = this.irc.isOnline(forUser.getCurrentNickName());
                
                if (!online || idle) {
                    this.deliverLater(remind, forUser, idle);
                } else {
                    this.deliverNowIrc(remind, forUser);
                }
            }
        } finally {
            this.deleteRemind(remind);            
        }
    }
    
    

    @Override
    public void deliverLater(final RemindEntity remind, User forUser, boolean wasIdle) 
                throws DatabaseException, EMailException {
        
        boolean asMail = forUser.getAttribute(MyPlugin.LEAVE_AS_MAIL).equals("true");
        
        if (asMail && wasIdle) {
            // user was idle and wanted email notification
            this.deliverNowIrc(remind, forUser);
            this.deliverNowMail(remind, forUser);
        } else if (asMail) {
            // user was offline and wanted email
            this.deliverNowMail(remind, forUser);
        } else if (wasIdle) {
            // user was online and wanted no email notification: notify now and when he 
            // returns
            this.deliverNowIrc(remind, forUser);
            RemindEntity onAction = new RemindEntity(remind.getMessage(), 
                remind.getFromUser(), 
                remind.getForUser(), 
                remind.getOnChannel(), 
                remind.getDueDate(), true);
            
            onAction.setIsMessage(true);
            this.addRemind(onAction, false);
        } else {
            RemindEntity message = new RemindEntity(remind.getMessage(), 
                remind.getFromUser(), 
                remind.getForUser(), 
                remind.getOnChannel(), 
                remind.getDueDate());
            message.setWasRemind(true);
            message.setIsMessage(true);
            this.addRemind(message, false);
        }
    }
    
    
    
    @Override
    public void deliverNowIrc(RemindEntity remind, User forUser) {
        RemindFormatter formatter = this.getFormat(forUser);
        
        String message = formatter.format(remind, this.formatter);
        boolean inChannel = this.irc.isOnChannel(
            remind.getOnChannel(), remind.getForUser());
    
        // If the user is not on the specified channel, the remind is delivered in query
        String channel = inChannel ? remind.getOnChannel() : remind.getForUser();
        // onAction messages are always delivered as qry
        if (remind.isOnAction()) {
            channel = remind.getForUser();
            this.onActionSet.take(remind.getForUser());
        }
        this.irc.sendMessage(channel, message, this);

        
        boolean qry = channel.equals(remind.getForUser());
        if (qry && (!remind.getForUser().equals(remind.getFromUser()))) {
            // send notice to user who left this remind if it was delivered in qry
            this.irc.sendMessage(remind.getFromUser(), "Deine Nachricht an '" + 
                remind.getForUser() + "' wurde zugestellt");
        }
        this.putToSleep(remind, forUser);
    }
    
    
    
    @Override
    public void deliverNowMail(RemindEntity remind, User forUser) 
                throws DatabaseException, EMailException {
        
        this.deleteRemind(remind);
        
        String mail = forUser.getAttribute(MyPlugin.EMAIL);
        if (mail.equals(MyPlugin.DEFAULT_EMAIL)) {
            RemindEntity r = new RemindEntity(
                "Deine E-Mail Nachricht an " + remind.getForUser() + 
                " konnte nicht zugestellt werden, da keine gültie E-mail Adresse " +
                "hinterlegt ist.", 
                this.irc.getNickname(), 
                remind.getFromUser(), 
                remind.getFromUser(),
                new Date());
            // schedule this Remind for now so it will be delivered immediately.
            // if user is not online, it will automatically be delivered later
            // by the policy implemented in #deliverLater
            this.addRemind(r, true);
        } else {
            String subject = String.format(SUBJECT, 
                this.formatter.formatDate(remind.getDueDate()));
            String message = MAIL_FORMAT.format(remind, this.formatter);
        
            this.mails.sendMail(mail, subject, message);
        }
    }

    
    
    @Override
    public void scheduleRemind(RemindEntity remind) {
        this.scheduleRemind(remind, remind.getDueDate());
    }

    
    
    @Override
    public void scheduleRemind(RemindEntity remind, Date dueDate) {
        RemindTask task = new RemindTask(this, remind);
        synchronized (this.scheduledReminds) {
            this.scheduledReminds.put(remind.getId(), task);
        }
        this.remindScheduler.schedule(task, remind.getDueDate());
    }

    
    
    @Override
    public void cancelScheduledRemind(RemindEntity remind) {
        this.cancelScheduledRemind(remind.getId());
    }

    
    
    @Override
    public void cancelScheduledRemind(int id) {
        RemindTask task = null;
        synchronized (this.scheduledReminds) {
            task = this.scheduledReminds.get(id);
            if (task != null) {
                task.cancel();
                this.scheduledReminds.remove(id);
            }
        }
    }

    
    
    @Override
    public void putToSleep(RemindEntity remind, User forUser) {
        synchronized (this.sleeping) {
            if (this.sleeping.containsKey(remind.getForUser())) {
                // remind already scheduled for sleep
                return;
            }
            
            this.sleeping.put(remind.getForUser(), remind);
        }
        // get sleep time:
        int sleepTime = Integer.parseInt(forUser.getAttribute(MyPlugin.SNOOZE_TIME));
        if (sleepTime > 0) {
            SleepTask task = new SleepTask(this, remind.getForUser());
            this.remindScheduler.schedule(task, sleepTime);
        }
    }
    
    
    
    @Override
    public RemindEntity cancelSleep(RemindEntity remind) {
        return this.cancelSleep(remind.getForUser());
    }
    
    
    
    @Override
    public RemindEntity cancelSleep(String forUser) {
        synchronized (this.sleeping) {
            return this.sleeping.remove(forUser);
        }
    }
    
    
    
    @Override
    public void snooze(User executor, Date dueDate) 
            throws CommandException, DatabaseException {
        RemindEntity existing;
        synchronized (this.sleeping) {
            existing = this.sleeping.get(executor.getCurrentNickName());
            this.sleeping.remove(executor.getCurrentNickName());
        }
        
        if (existing == null) {
            throw new CommandException("Es existiert kein Remind für dich das du " +
            		"verlängern kannst");
        }
        
        RemindEntity newRemind = existing.copyForNewDueDate(dueDate);
        this.addRemind(newRemind, true);
    }
    
    
    @Override
    public RemindEntity toggleIsMail(User executor, int id)
            throws DatabaseException, CommandException {
        final RemindEntity remind = this.persistence.atomicRetrieveSingle(
            RemindEntity.class, id);
        
        this.checkRemind(executor, remind, id);
        this.persistence.atomicWriteOperation(new WriteAction() {
            @Override
            public void performUpdate(PersistenceManager persistence) {
                remind.setIsMail(!remind.isMail());
            }
        });
        return remind;
    }
    
    
    
    @Override
    public User getUser(String nickName) {
        User u = this.userManager.getUser(nickName);
        if (u == null) {
            u = this.userManager.createUser(nickName, "", 0);
            u.setCurrentNickName(nickName);
        }
        
        return u;
    }

    
    
    @Override
    public RemindFormatter getFormat(User user) {
        RemindFormatter special = this.specialFormats.get(
            user.getCurrentNickName().toLowerCase());
        if (special != null) {
            return special;
        }
        
        String pattern = user.getAttribute(MyPlugin.REMIND_FORMAT_NAME);
        if (pattern == null) {
            return DEFAULT_FORMAT;
        }
        return PatternRemindFormatter.forPattern(pattern, true);
    }



    @Override
    public boolean isIdle(User user) {
        long lastMsg = Long.parseLong(user.getAttribute(MyPlugin.REMIND_IDLE_TIME));
        return System.currentTimeMillis() - user.getLastMessageTime() > lastMsg;
    }
    
    
    
    @Override
    public boolean isOnActionAvailable(String forUser) {
        return this.onActionSet.available(forUser);
    }
    
    
    
    @Override
    public void modifyRemind(User executor, int id, final Date dueDate, final String msg)
            throws CommandException, DatabaseException {
        
        final RemindEntity remind = this.dbWrapper.getRemind(id);
    
        this.checkRemind(executor, remind, id);
        this.cancelScheduledRemind(id);
        this.persistence.atomicWriteOperation(new WriteAction() {
        
        @Override
        public void performUpdate(PersistenceManager persistence) {
            remind.setDueDate(dueDate);
            remind.setMessage(msg);
        }
        });
        this.scheduleRemind(remind, dueDate);
    }
    
    
    
    @Override
    public boolean canEdit(User user, RemindEntity remind) {
        return user.getUserLevel() >= UserManager.ADMIN ||
                remind.getForUser().equals(user.getCurrentNickName()) ||
                remind.getFromUser().equals(user.getCurrentNickName());
    }
    
    
    
    @Override
    public void checkRemind(User user, RemindEntity remind, int id) 
            throws CommandException {
        if (remind == null) {
            throw new CommandException("Kein Remind mit der ID " + id);
        } else if (!canEdit(user, remind)) {
            throw new CommandException("Du kannst das Remind mit der ID " + id + 
                " nicht ändern oder löschen");
        }
    }
    
    
    
    @Override
    public void traceNickChange(IrcUser oldUser, final IrcUser newUser) {
        User oldForUser = this.getUser(oldUser.getNickName());
        if (oldForUser.getAttribute(MyPlugin.REMIND_TRACK_NICKCHANGE).equals("false")) {
            return;
        }
        
        final List<RemindEntity> reminds = this.dbWrapper.getRemindsForUser(
            oldUser.getNickName());
        if (reminds.isEmpty()) {
            return;
        }
        
        User newForUser = this.getUser(newUser.getNickName());
        
        try {
            // HACK: this resets the sleep time
            RemindEntity sleeping = this.cancelSleep(oldUser.getNickName());
            if (sleeping != null) {
                this.putToSleep(sleeping, newForUser);
            }
            
            this.persistence.atomicWriteOperation(new WriteAction() {
                
                @Override
                public void performUpdate(PersistenceManager persistence) {
                    for (RemindEntity remind : reminds) {
                        remind.setForUser(newUser.getNickName());
                    }
                }
            });
        } catch (DatabaseException e) {
            
        }
    }
    
    
    
    @Override
    public void rescheduleAll() {
        List<RemindEntity> allReminds = this.dbWrapper.getAllReminds();
        for (RemindEntity remind : allReminds) {
            this.scheduleRemind(remind);
        }
    }
    
    
    
    @Override
    protected void actualDispose() throws DisposingException {
        this.remindScheduler.cancel();
        this.sleeping.clear();
        this.onActionSet.clear();
        this.scheduledReminds.clear();
        this.specialFormats.clear();
    }
}