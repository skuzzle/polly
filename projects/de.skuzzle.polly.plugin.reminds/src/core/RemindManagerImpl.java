package core;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.apache.log4j.Logger;

import polly.reminds.MSG;
import polly.reminds.MyPlugin;
import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.MailManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Atomic;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.Types.BooleanType;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.Types.TimespanType;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.eventlistener.IrcUser;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.EMailException;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.time.Milliseconds;
import de.skuzzle.polly.sdk.time.Time;
import entities.RemindEntity;



public class RemindManagerImpl extends AbstractDisposable implements RemindManager {
    
    private final static RemindFormatter MAIL_FORMAT = new MailRemindFormatter();
    
    private final static String SUBJECT = MSG.remindMngrSubject;
    
    private final static long AUTO_SNOOZE_WAIT_TIME = Milliseconds.fromMinutes(5);
    
    public final static RemindFormatter DEFAULT_FORMAT = 
        PatternRemindFormatter.forPattern(MyPlugin.REMIND_FORMAT_VALUE.getValue());
    
    // XXX: special case for clum
    private final static RemindFormatter heidiFormat = new HeidiRemindFormatter();
    
    private final Map<User, RemindEntity> lastReminds;
    
    private MailManager mails;
    private IrcManager irc;
    private PersistenceManagerV2 persistence;
    private UserManager userManager;
    private RoleManager roleManager;
    private Timer remindScheduler;
    private Map<Integer, RemindTask> scheduledReminds;
    private Map<String, RemindFormatter> specialFormats;
    private Map<String, RemindEntity> sleeping;
    private ActionCounter actionCounter;
    private ActionCounter messageCounter;
    private FormatManager formatter;
    private RemindDBWrapper dbWrapper;
    private Logger logger;
    
    
    
    public RemindManagerImpl(MyPolly myPolly) {
        this.mails = myPolly.mails();
        this.irc = myPolly.irc();
        this.persistence = myPolly.persistence();
        this.userManager = myPolly.users();
        this.formatter = myPolly.formatting();
        this.roleManager = myPolly.roles();
        this.lastReminds = new HashMap<User, RemindEntity>();
        this.dbWrapper = new RemindDBWrapperImpl(myPolly.persistence());
        this.remindScheduler = new Timer("REMIND_SCHEDULER", true); //$NON-NLS-1$
        this.scheduledReminds = new HashMap<Integer, RemindManager.RemindTask>();
        this.specialFormats = new HashMap<String, RemindFormatter>();
        this.sleeping = new HashMap<String, RemindEntity>();
        this.actionCounter = new ActionCounter();
        this.messageCounter = new ActionCounter();
        this.logger = Logger.getLogger(myPolly.getLoggerName(this.getClass()));
        
        // XXX: special case for clum:
        this.specialFormats.put("clum", heidiFormat); //$NON-NLS-1$
    }
    
    
    
    @Override
    public RemindDBWrapper getDatabaseWrapper() {
        return this.dbWrapper;
    }
    
    
    
    @Override
    public synchronized RemindEntity getLastRemind(User user) {
        return this.lastReminds.get(user);
    }
    
    
    
    @Override
    public synchronized void addRemind(User executor, final RemindEntity remind, 
            boolean schedule) throws DatabaseException {
        logger.info("Adding " + remind + ", schedule: " + schedule); //$NON-NLS-1$ //$NON-NLS-2$
        this.dbWrapper.addRemind(remind);
        this.lastReminds.put(executor, remind);
        
        if (schedule) {
            this.scheduleRemind(remind);
        }
        
        if (remind.isOnAction()) {
            logger.trace("Storing remind as on return action."); //$NON-NLS-1$
            this.actionCounter.put(remind.getForUser());
        }
        if (remind.isMessage()) {
            logger.trace("Storing remind as leave message."); //$NON-NLS-1$
            this.messageCounter.put(remind.getForUser());
        }
    }
    
    

    @Override
    public void deleteRemind(int id) throws DatabaseException {
        RemindEntity remind = this.dbWrapper.getRemind(id);
        logger.trace("Deleting remind with id " + id); //$NON-NLS-1$
        this.deleteRemind(remind);
    }

    
    
    @Override
    public synchronized void deleteRemind(RemindEntity remind) throws DatabaseException {
        if (remind != null) {
            this.cancelScheduledRemind(remind.getId());
            this.dbWrapper.deleteRemind(remind);
            Iterator<RemindEntity> it = this.lastReminds.values().iterator();
            while(it.hasNext()) {
                if (it.next() == remind) {
                    it.remove();
                }
            }
        } else {
            logger.warn("tried to delete non-existent remind."); //$NON-NLS-1$
        }
    }
    
    
    
    @Override
    public void deleteRemind(User executor, int id) 
            throws CommandException, DatabaseException {
        logger.debug("User '" + executor + " wants to delete remind with id " + id); //$NON-NLS-1$ //$NON-NLS-2$
        RemindEntity remind = this.dbWrapper.getRemind(id);
        this.checkRemind(executor, remind, id);
        this.deleteRemind(remind);
    }
    
    
    
    @Override
    public void deleteRemind(User executor) throws DatabaseException {
        final RemindEntity re = this.lastReminds.get(executor);
        if (re == null) {
            throw new DatabaseException(MSG.remindMngrNoRemind);
        }
        this.deleteRemind(re);
    }
    
    
    
    @Override
    public synchronized void deliverRemind(RemindEntity remind, boolean checkIdleStatus) 
            throws DatabaseException, EMailException {
        User forUser = this.getUser(remind.getForUser());
        
        logger.info("Trying to deliver " + remind + " for " + forUser); //$NON-NLS-1$ //$NON-NLS-2$
        try {
            if (remind.isMail()) {
                this.deliverNowMail(remind, forUser, false);
            } else {
                logger.trace("Remind is to be delivered in IRC. Checking user state"); //$NON-NLS-1$
                boolean idle = this.isIdle(forUser) && checkIdleStatus;
                boolean online = this.irc.isOnline(forUser.getCurrentNickName());
                logger.trace("Idle state: " + idle + ", online state: " + online); //$NON-NLS-1$ //$NON-NLS-2$
                
                
                if (!online || idle) {
                    this.deliverLater(remind, forUser, idle, online);
                } else {
                    this.deliverNowIrc(remind, forUser, online);
                }
            }
        } finally {
            logger.trace("Now deleting " + remind); //$NON-NLS-1$
            this.deleteRemind(remind);            
        }
    }
    
    

    @Override
    public void deliverLater(final RemindEntity remind, User forUser, boolean wasIdle, 
                boolean online) throws DatabaseException, EMailException {
        logger.trace("Delivering later. Checking if remind schould be delivered as mail"); //$NON-NLS-1$
        boolean asMail = ((BooleanType) forUser.getAttribute(
            MyPlugin.LEAVE_AS_MAIL)).getValue();
        boolean doubleDelivery = ((BooleanType) forUser.getAttribute(
                MyPlugin.REMIND_DOUBLE_DELIVERY)).getValue();
        
        logger.trace("As Mail: " + asMail); //$NON-NLS-1$
        logger.trace("Double-delivery: " + doubleDelivery); //$NON-NLS-1$
        
        if (asMail && wasIdle) {
            // user was idle and wanted email notification
            this.deliverNowIrc(remind, forUser, online);
            this.deliverNowMail(remind, forUser, wasIdle);
        } else if (asMail) {
            // user was offline and wanted email
            this.deliverNowMail(remind, forUser, wasIdle);
        } else if (wasIdle) {
            // user was online and wanted no email notification: notify now and when he 
            // returns
            this.deliverNowIrc(remind, forUser, online);
            if (doubleDelivery) {
                RemindEntity onAction = new RemindEntity(remind.getMessage(), 
                    remind.getFromUser(), 
                    remind.getForUser(), 
                    remind.getOnChannel(), 
                    remind.getDueDate(), true,
                    remind.getLeaveDate());
                
                onAction.setIsMessage(true);
                this.addRemind(forUser, onAction, false);
            }
        } else {
            RemindEntity message = new RemindEntity(remind.getMessage(), 
                remind.getFromUser(), 
                remind.getForUser(), 
                remind.getOnChannel(), 
                remind.getDueDate(),
                remind.getLeaveDate());
            message.setWasRemind(true);
            message.setIsMessage(true);
            this.addRemind(forUser, message, false);
        }
    }
    
    
    
    @Override
    public void deliverNowIrc(RemindEntity remind, User forUser, boolean online) {
        if (!online) {
            return;
        }
        logger.trace("Delivering " + remind + " now in IRC"); //$NON-NLS-1$ //$NON-NLS-2$
        RemindFormatter formatter = this.getFormat(forUser);
        
        String message = formatter.format(remind, this.formatter);
        boolean inChannel = this.irc.isOnChannel(
            remind.getOnChannel(), remind.getForUser());
    
        // If the user is not on the specified channel, the remind is delivered in query
        String channel = inChannel ? remind.getOnChannel() : remind.getForUser();
        // onAction messages are always delivered as qry
        if (remind.isOnAction()) {
            logger.trace("Remind was onAction. Removing it from onActionSet"); //$NON-NLS-1$
            channel = remind.getForUser();
            this.actionCounter.take(remind.getForUser());
        }

        // decrease counter of undelivered reminds for that user
        if (remind.isMessage()) {
            this.messageCounter.take(remind.getForUser());
        }
        this.irc.sendMessage(channel, message, this);

        
        boolean qry = channel.equals(remind.getForUser());
        if (qry && (!remind.getForUser().equals(remind.getFromUser()))) {
            // send notice to user who left this remind if it was delivered in qry
            this.irc.sendMessage(remind.getForUser(), 
                    MSG.bind(MSG.remindMngrDelivered, remind.getForUser(), 
                            remind.getMessage()), this);
        }
        this.putToSleep(remind, forUser);
        this.checkTriggerAutoSnooze(forUser);
    }
    
    
    
    private final void checkTriggerAutoSnooze(User forUser) {
        final BooleanType autoSnooze = (BooleanType) forUser.getAttribute(
            MyPlugin.AUTO_SNOOZE);
        
        if (!this.userManager.isSignedOn(forUser)) {
            return;
        } else if (!autoSnooze.getValue()) {
            return;
        }
        final String indicator = ((StringType) forUser.getAttribute(
            MyPlugin.AUTO_SNOOZE_INDICATOR)).getValue();
        this.irc.sendMessage(forUser.getCurrentNickName(), 
                MSG.bind(MSG.remindMngrAutoSnoozeActive, indicator), this);
        
        new AutoSnoozeRunLater("AUTO_SNOOZE_WAITER", forUser,  //$NON-NLS-1$
            AUTO_SNOOZE_WAIT_TIME, this.irc, this, this.formatter).start();
    }
    
    
    
    @Override
    public void deliverNowMail(RemindEntity remind, User forUser, boolean wasIdle) 
                throws DatabaseException, EMailException {
        logger.trace("Delivering " + remind + " now as mail"); //$NON-NLS-1$ //$NON-NLS-2$
        
        String mail = ((StringType) forUser.getAttribute(MyPlugin.EMAIL)).getValue();
        if (mail.equals(MyPlugin.DEFAULT_EMAIL)) {
            logger.warn("Destination user has no valid email address set"); //$NON-NLS-1$
            RemindEntity r = new RemindEntity(
                MSG.bind(MSG.remindMngrMailFail, remind.getForUser()), 
                this.irc.getNickname(), 
                remind.getFromUser(), 
                remind.getFromUser(),
                Time.currentTime(),
                Time.currentTime());
            // schedule this Remind for now so it will be delivered immediately.
            // if user is not online, it will automatically be delivered later
            // by the policy implemented in #deliverLater
            this.addRemind(forUser, r, true);
        } else {
            String subject = String.format(SUBJECT, 
                remind.getMessage(),
                this.formatter.formatDate(remind.getDueDate()));
            String message = MAIL_FORMAT.format(remind, this.formatter);
            
            // if user was online, wait for reaction before sending remind as mail
            if (wasIdle) {
                new MailRunLater(forUser, 
                    this.irc, 
                    this.mails, 
                    subject, 
                    message, 
                    mail).start();
            } else {
                this.mails.sendMail(mail, subject, message);
            }
        }
    }

    
    
    @Override
    public void scheduleRemind(RemindEntity remind) {
        this.scheduleRemind(remind, remind.getDueDate());
    }

    
    
    @Override
    public void scheduleRemind(RemindEntity remind, Date dueDate) {
        logger.trace("Scheduling remind " + remind + ". Due date: " + dueDate); //$NON-NLS-1$ //$NON-NLS-2$
        RemindTask task = new RemindTask(this, remind);
        synchronized (this.scheduledReminds) {
            this.scheduledReminds.put(remind.getId(), task);
        }
        this.remindScheduler.schedule(task, dueDate);
    }

    
    
    @Override
    public void cancelScheduledRemind(RemindEntity remind) {
        this.cancelScheduledRemind(remind.getId());
    }

    
    
    @Override
    public void cancelScheduledRemind(int id) {
        logger.trace("Cancelling scheduled remind with id " + id); //$NON-NLS-1$
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
        logger.trace("Remembering " + remind + " for snooze"); //$NON-NLS-1$ //$NON-NLS-2$
        synchronized (this.sleeping) {
            this.sleeping.put(remind.getForUser(), remind);
        }
        // get sleep time:
        final TimespanType sleepTime = (TimespanType) 
            forUser.getAttribute(MyPlugin.SNOOZE_TIME);
        
        logger.trace("Snooze time for " + forUser + ": " + sleepTime); //$NON-NLS-1$ //$NON-NLS-2$
        if (sleepTime.getSpan() > 0) {
            SleepTask task = new SleepTask(this, remind.getForUser());
            this.remindScheduler.schedule(task, sleepTime.getSpan() * 1000);
        }
    }
    
    
    
    @Override
    public RemindEntity cancelSleep(RemindEntity remind) {
        return this.cancelSleep(remind.getForUser());
    }
    
    
    
    @Override
    public RemindEntity cancelSleep(String forUser) {
        logger.trace("Cancelling snooze for user " + forUser); //$NON-NLS-1$
        synchronized (this.sleeping) {
            return this.sleeping.remove(forUser);
        }
    }
    
    
    
    @Override
    public RemindEntity snooze(User executor, Date dueDate) 
            throws CommandException, DatabaseException {
        
        logger.trace("User " + executor + " requested snooze"); //$NON-NLS-1$ //$NON-NLS-2$
        RemindEntity existing;
        synchronized (this.sleeping) {
            existing = this.sleeping.get(executor.getCurrentNickName());
            this.cancelSleep(executor.getCurrentNickName());
        }
        
        if (existing == null) {
            throw new CommandException(MSG.remindMngrNoSnooze);
        }
        
        // if no explicit date is given, schedule new remind as long as the old was 
        // running
        if (dueDate == null) {
            logger.trace("No duedate given. Calculating runtime of remind to snooze."); //$NON-NLS-1$
            long runtime = existing.getDueDate().getTime() - 
                existing.getLeaveDate().getTime();
            dueDate = new Date(Time.currentTimeMillis() + runtime);
            logger.trace("Remind runtime is: " + this.formatter.formatTimeSpanMs(runtime)); //$NON-NLS-1$
        }
        
        RemindEntity newRemind = existing.copyForNewDueDate(dueDate);
        this.addRemind(executor, newRemind, true);
        return newRemind;
    }
    
    
    
    @Override
    public RemindEntity snooze(User executor) throws DatabaseException,
            CommandException {
        
        boolean useSnoozeTime = ((BooleanType) executor.getAttribute(
            MyPlugin.USE_SNOOZE_TIME)).getValue();
        
        if (useSnoozeTime) {
            TimespanType defaultRemindTime = (TimespanType) 
                executor.getAttribute(MyPlugin.DEFAULT_REMIND_TIME);
            return this.snooze(executor, 
                new Date(Time.currentTimeMillis() + defaultRemindTime.getSpan() * 1000));
        } else {
            return this.snooze(executor, null);
        }
    }
    
    
    
    @Override
    public RemindEntity getSnoozabledRemind(String name) {
        synchronized (this.sleeping) {
            return this.sleeping.get(name);
        }
    }
    
    
    
    @Override
    public RemindEntity toggleIsMail(User executor, int id)
            throws DatabaseException, CommandException {
        final RemindEntity remind = this.persistence.atomic().find(
            RemindEntity.class, id);
        
        logger.trace("Toggeling delivery of " + remind); //$NON-NLS-1$
        this.checkRemind(executor, remind, id);
        this.persistence.writeAtomic(new Atomic() {
            @Override
            public void perform(Write write) {
                remind.setIsMail(!remind.isMail());
            }
        });
        logger.trace("New delivery type: " + (remind.isMail() ? "Mail" : "IRC")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return remind;
    }
    
    
    
    @Override
    public User getUser(String nickName) {
        logger.trace("Getting user for name '" + nickName + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        User u = this.userManager.getUser(nickName);
        if (u == null) {
            logger.trace("User is unknown, creating new one"); //$NON-NLS-1$
            u = this.userManager.createUser(nickName, ""); //$NON-NLS-1$
        }
        if (u.getCurrentNickName() == null) {
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
        
        final String pattern = ((StringType) 
            user.getAttribute(MyPlugin.REMIND_FORMAT_NAME)).getValue();
        if (pattern == null) {
            return DEFAULT_FORMAT;
        }
        return PatternRemindFormatter.forPattern(pattern, true);
    }



    @Override
    public boolean isIdle(User user) {
        final TimespanType remindIdleTime = (TimespanType) 
            user.getAttribute(MyPlugin.REMIND_IDLE_TIME); 
        return Time.currentTimeMillis() - user.getLastMessageTime() > 
            Milliseconds.fromSeconds(remindIdleTime.getSpan());
    }
    
    
    
    @Override
    public boolean isOnActionAvailable(String forUser) {
        return this.actionCounter.available(forUser);
    }
    
    
    
    @Override
    public boolean isStale(String forUser) {
        return this.messageCounter.available(forUser);
    }
    
    
    
    @Override
    public RemindEntity modifyRemind(User executor, int id, final Date dueDate, final String msg)
            throws CommandException, DatabaseException {
        logger.trace("User " + executor + " requested to modify remind with id " + id); //$NON-NLS-1$ //$NON-NLS-2$
        final RemindEntity remind = this.dbWrapper.getRemind(id);
    
        this.checkRemind(executor, remind, id);
        this.cancelScheduledRemind(id);
        this.persistence.writeAtomic(new Atomic() {
            @Override
            public void perform(Write write) {
                if (dueDate != null) {
                    remind.setDueDate(dueDate);
                }
                if (msg != null) {
                    remind.setMessage(msg);
                }
            }
        });
        this.scheduleRemind(remind, dueDate == null ? remind.getDueDate() : dueDate);
        return remind;
    }
    
    
    
    @Override
    public boolean canEdit(User user, RemindEntity remind) {
        return remind.getForUser().equals(user.getCurrentNickName()) ||
            remind.getFromUser().equals(user.getCurrentNickName()) ||
            this.roleManager.hasPermission(user, 
                MyPlugin.MODIFY_OTHER_REMIND_PERMISSION);
    }
    
    
    
    @Override
    public void checkRemind(User user, RemindEntity remind, int id) 
            throws CommandException {
        logger.trace("Checking for sufficient rights of user " + user + " for " + remind); //$NON-NLS-1$ //$NON-NLS-2$
        if (remind == null) {
            throw new CommandException(MSG.bind(MSG.remindMngrNoRemindWithId, id));
        } else if (!canEdit(user, remind)) {
            throw new CommandException(MSG.bind(MSG.remindMngrNoPermission, id));
        }
    }
    
    
    
    @Override
    public void traceNickChange(IrcUser oldUser, final IrcUser newUser) {
        logger.trace("tracing nickchange " + oldUser + " -> " + newUser); //$NON-NLS-1$ //$NON-NLS-2$
        User oldForUser = this.getUser(oldUser.getNickName());
        final BooleanType track = (BooleanType) oldForUser.getAttribute(
            MyPlugin.REMIND_TRACK_NICKCHANGE); 
        
        if (!track.getValue()) {
            logger.trace("User doesnt want this nickchange to be tracked"); //$NON-NLS-1$
            return;
        }
        
        final List<RemindEntity> reminds = this.dbWrapper.getRemindsForUser(
            oldUser.getNickName());
        
        User newForUser = this.getUser(newUser.getNickName());
        this.actionCounter.moveUser(oldUser.getNickName(), newUser.getNickName());
        this.messageCounter.moveUser(oldUser.getNickName(), newUser.getNickName());
        
        try {
            // HACK: this resets the sleep time
            RemindEntity sleeping = this.cancelSleep(oldUser.getNickName());
            if (sleeping != null) {
                this.putToSleep(sleeping, newForUser);
            }
            
            if (reminds.isEmpty()) {
                return;
            }
            
            this.persistence.writeAtomic(new Atomic() {
                @Override
                public void perform(Write write) {
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
        logger.trace("Scheduling all existing reminds for their duedate"); //$NON-NLS-1$
        List<RemindEntity> allReminds = this.dbWrapper.getAllReminds();
        synchronized (this.scheduledReminds) {
            for (RemindEntity r : allReminds) {
                logger.trace("Scheduling remind " + r + ". Due date: " + r.getDueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                RemindTask task = new RemindTask(this, r);
                this.scheduledReminds.put(r.getId(), task);
                this.remindScheduler.schedule(task, r.getDueDate());
                
                if (r.isMessage()) {
                    this.messageCounter.put(r.getForUser());
                }
                if (r.isOnAction()) {
                    this.actionCounter.put(r.getForUser());
                }
            }
        }
    }
    
    
    
    @Override
    protected void actualDispose() throws DisposingException {
        this.remindScheduler.cancel();
        this.sleeping.clear();
        this.actionCounter.clear();
        this.scheduledReminds.clear();
        this.specialFormats.clear();
    }
}