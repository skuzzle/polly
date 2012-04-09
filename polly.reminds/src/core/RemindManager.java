package core;


import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import org.apache.log4j.Logger;

import polly.reminds.MyPlugin;

import de.skuzzle.polly.sdk.AbstractDisposable;
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



/**
 * 
 * @author Simon
 * @version 27.07.2011 e1a9f7c
 */
public class RemindManager extends AbstractDisposable {

    private Logger logger;
    private PersistenceManager persistence;
    private MyPolly myPolly;
    private Timer remindScheduler;
    private Map<Integer, RemindTask> reminds;
    private Map<String, RemindEntity> sleeps;
    private Set<String> onReturnAvailable;
    
    // XXX: special case for user clum, whose reminds will be delivered to several
    //      random nicknames
    private RemindFormatter heidiFormatter;
    
    private final static RemindFormatter DEFAULT_FORMAT = 
            PatternRemindFormatter.forPattern(MyPlugin.REMIND_FORMAT_VALUE);
    
    
    private final static RemindFormatter MAIL_FORMAT = new MailRemindFormatter();
    
    private final static String SUBJECT = "[Reminder] Erinnerung um %s";
    
    
    
    public RemindManager(MyPolly myPolly) {
        this.logger = Logger.getLogger(myPolly.getLoggerName(this.getClass()));
        this.persistence = myPolly.persistence();
        this.myPolly = myPolly;
        this.remindScheduler = new Timer("REMIND_SCHEDULER");
        this.reminds = Collections.synchronizedMap(new HashMap<Integer, RemindTask>());
        this.sleeps = Collections.synchronizedMap(new HashMap<String, RemindEntity>());
        this.onReturnAvailable = new HashSet<String>();
        this.heidiFormatter = new HeidiRemindFormatter();
    }

    
    
    public synchronized void scheduleRemind(RemindEntity remind, Date dueDate) {
        logger.info("Scheduling Remind: " + remind);
        RemindTask task = new RemindTask(remind, this, this.logger);
        this.remindScheduler.schedule(task, dueDate);
        this.reminds.put(remind.getId(), task);
    }
    
    
    
    public RemindEntity getRemind(int id) {
        return this.persistence.atomicRetrieveSingle(RemindEntity.class, id);
    }
    
    
    
    public synchronized void unSchedule(int remindId) {
        RemindTask t = this.reminds.get(remindId);
        if (t != null) {
            logger.debug("Cancelling remind task for remind id " + remindId);
            t.cancel();
            this.reminds.remove(remindId);
        }
    }
    
    
    
    public synchronized void deliverRemind(final RemindEntity remind) 
            throws DatabaseException, EMailException {
        logger.debug("Executing Remind: " + remind);
        this.persistence.refresh(remind);
        
        if (remind.isMail()) {
            this.deliverByMail(remind);
            return;
        }
        
        if (!this.myPolly.irc().isOnline(remind.getForUser())) {
            if (this.checkAttribute(remind.getForUser(), MyPlugin.LEAVE_AS_MAIL)) {
                logger.debug("User is not online. Remind is delivered by mail");
                this.deliverByMail(remind);
                return;
            }
            logger.debug("User is not online. Remind will be delivered when he returns.");
            try {
                this.persistence.atomicWriteOperation(new WriteAction() {
                    
                    @Override
                    public void performUpdate(PersistenceManager persistence) {
                        remind.setIsMessage(true);
                        remind.setWasRemind(true);
                    }
                });
            } catch (Exception e) {
                logger.error("", e);
            }
            return;
        }
        
        RemindFormatter formatter = RemindManager.DEFAULT_FORMAT;
        IrcUser tmp = new IrcUser(remind.getForUser(), "", "");
        if (this.myPolly.users().isSignedOn(tmp)) {
            User user = this.myPolly.users().getUser(tmp);
            formatter = this.formatForUser(user);
        }
        
        String message = formatter.formatRemind(remind, this.myPolly.formatting());
    
        boolean inChannel = this.myPolly.irc().isOnChannel(
                remind.getOnChannel(), remind.getForUser());
        
        // If the user is not on the specified channel, the remind is delivered in query
        String destination = inChannel ? remind.getOnChannel() : remind.getForUser();
        
        // OnReturn messages are always delivered in query
        if (remind.isOnAction()) {
            destination = remind.getForUser();
            this.onReturnAvailable.remove(remind.getForUser());
        }

        // determine whether this remind is delivered in qry
        boolean isQuery = destination.equals(remind.getForUser());
        
        myPolly.irc().sendMessage(destination, message, this);

        if (isQuery && (!remind.getForUser().equals(remind.getFromUser()))) {
            // send notice to user who left this remind if it was delivered in qry
            this.myPolly.irc().sendMessage(remind.getFromUser(), "Deine Nachricht an '" + 
                remind.getForUser() + "' wurde zugestellt");
        }
        
        this.putToSleep(remind);
        this.deleteRemind(remind);
    }
    
    
    
    private void deliverByMail(RemindEntity remind) throws DatabaseException, 
            EMailException {
        
        this.deleteRemind(remind);
        User user = this.myPolly.users().getUser(remind.getForUser());
        if (user == null) {
            throw new EMailException("Unbekannter Benutzer: " + remind.getForUser());
        }
        
        String mail = user.getAttribute(MyPlugin.EMAIL);
        
        if (mail.equals(MyPlugin.DEFAULT_EMAIL)) {
            if (this.myPolly.irc().isOnline(remind.getFromUser())) {
                this.myPolly.irc().sendMessage(remind.getFromUser(), 
                    "Deine E-Mail Nachricht an " + remind.getForUser() + 
                    " konnte nicht zugestellt werden, da keine gültie E-mail Adresse " +
                    "hinterlegt ist.", this);
            } else {
                RemindEntity r = new RemindEntity(
                    "Deine E-Mail Nachricht an " + remind.getForUser() + 
                    " konnte nicht zugestellt werden, da keine gültie E-mail Adresse " +
                    "hinterlegt ist.", 
                    this.myPolly.irc().getNickname(), 
                    remind.getFromUser(), 
                    remind.getFromUser(),
                    new Date());
                r.setIsMessage(true);
                this.addRemind(r);
            }
            return;
        }
        String subject = String.format(SUBJECT, 
                this.myPolly.formatting().formatDate(remind.getDueDate()));
        String message = MAIL_FORMAT.format(remind, this.myPolly.formatting());
        
        this.myPolly.mails().sendMail(mail, subject, message);
    }
    
    
    
    private boolean checkAttribute(String forUser, String booleanAttributeName) {
        User user = this.myPolly.users().getUser(forUser);
        if (user == null) {
            return false;
        }
        return user.getAttribute(booleanAttributeName).equals("true");
    }
    
    
    
    public boolean canEdit(RemindEntity remind, User user) {
        return user.getUserLevel() >= UserManager.ADMIN ||
            remind.getForUser().equals(user.getCurrentNickName()) ||
            remind.getFromUser().equals(user.getCurrentNickName());
    }
    
    
    
    private void putToSleep(RemindEntity remind) {
        String sleepString = MyPlugin.SLEEP_DEFAULT_VALUE;
        
        IrcUser tmp = new IrcUser(remind.getForUser(), "", "");
        if (this.myPolly.users().isSignedOn(tmp)) {
            User user = this.myPolly.users().getUser(tmp);
            
            try {
                this.persistence.readLock();
                sleepString = user.getAttribute(MyPlugin.SLEEP_TIME);
            } finally {
                this.persistence.readUnlock();
            }
        }
        
        int sleepTime = Integer.parseInt(sleepString);
        this.sleeps.put(remind.getForUser(), remind);
        if (sleepTime > 0) {
            logger.trace("Scheduling remind for snooze for " + sleepString + "ms.");
            this.remindScheduler.schedule(new SleepTask(remind.getForUser(), this), 
                sleepTime);
        } else {
            logger.trace("Remind will be sleepable forever");
        }
    }
    
    
    
    private RemindFormatter formatForUser(User user) {
        // XXX: special case for user clum
        if (user.getCurrentNickName().equalsIgnoreCase("clum")) {
            return this.heidiFormatter;
        }
        
        String pattern = user.getAttribute(MyPlugin.REMIND_FORMAT_NAME);
        if (pattern == null) {
            return RemindManager.DEFAULT_FORMAT;
        }
        return PatternRemindFormatter.forPattern(pattern, true);
    }
    
    
    
    public boolean onReturnAvailable(String user) {
        return this.onReturnAvailable.contains(user);
    }
    
    
    
    public RemindEntity getSleep(String nickName) {
        return this.sleeps.get(nickName);
    }
    
    
    
    public void removeSleep(String nickName) {
        this.sleeps.remove(nickName);
    }
    
    
    
    private void checkRemind(int id, RemindEntity remind, User user) 
            throws CommandException {
        if (remind == null) {
            throw new CommandException("Kein Remind mit der ID " + id);
        } else if (!canEdit(remind, user)) {
            throw new CommandException("Du kannst das Remind mit der ID " + id + 
                " nicht ändern");
        }
    }
    
    
    
    public RemindEntity toggleIsMail(User executor, int id) 
            throws CommandException, DatabaseException {
        final RemindEntity remind = this.persistence.atomicRetrieveSingle(
            RemindEntity.class, id);
        
        this.checkRemind(id, remind, executor);
        this.persistence.atomicWriteOperation(new WriteAction() {
            
            @Override
            public void performUpdate(PersistenceManager persistence) {
                remind.setIsMail(!remind.isMail());
            }
        });
        return remind;
    }
    
    
    
    public void modifyRemind(User executor, int id, final Date dueDate, 
            final String msg) throws CommandException, DatabaseException {
        final RemindEntity remind = this.persistence.atomicRetrieveSingle(
                RemindEntity.class, id);
        
        checkRemind(id, remind, executor);
        this.unSchedule(id);
        this.persistence.atomicWriteOperation(new WriteAction() {
            
            @Override
            public void performUpdate(PersistenceManager persistence) {
                remind.setDueDate(dueDate);
                remind.setMessage(msg);
            }
        });
        this.scheduleRemind(remind, dueDate);
    }
    
    
    
    public List<RemindEntity> undeliveredReminds(IrcUser user) {

        try {
            this.persistence.readLock();
            return this.persistence.findList(RemindEntity.class, "UNDELIVERED_FOR_USER", 
                    user.getNickName());
        } finally {
            this.persistence.readUnlock();
        }
    }
    
    
    
    public void addRemind(RemindEntity remind) throws DatabaseException {
        logger.debug("Adding Remind: " + remind);
        
        if (remind.isOnAction()) {
            this.onReturnAvailable.add(remind.getForUser());
        }
        
        this.persistence.atomicPersist(remind);
    }

    
    private void deleteRemind(RemindEntity remind) throws DatabaseException {
        this.reminds.remove(remind);
        this.persistence.atomicRemove(remind);
    }
    
    
    public void deleteRemind(final int id, User executor) 
                throws CommandException, DatabaseException {
        final RemindEntity remind = this.persistence.atomicRetrieveSingle(
            RemindEntity.class, id);
        
        this.checkRemind(id, remind, executor);
        
        
        logger.debug("Removing Remind with id " + id);
        this.unSchedule(id);
        this.reminds.remove(id);
        persistence.atomicWriteOperation(new WriteAction() {
            
            @Override
            public void performUpdate(PersistenceManager persistence) {
                persistence.remove(remind);
            }
        });
    }
    
    
    
    public List<RemindEntity> getAllReminds() {
        try {
            this.persistence.readLock();
            return this.persistence.findList(RemindEntity.class, "ALL_REMINDS");
        } finally {
            this.persistence.readUnlock();
        }
    }
    
    
    
    public List<RemindEntity> getRemindsForUser(String nickName) {
        try {
            this.persistence.readLock();
            return this.persistence.findList(
                    RemindEntity.class, "REMIND_FOR_USER", nickName);
        } finally {
            this.persistence.readUnlock();
        }
    }
    
    
    
    public List<RemindEntity> getMyReminds(String nickName) {
        try {
            this.persistence.readLock();
            return this.persistence.findList(
                    RemindEntity.class, "MY_REMIND_FOR_USER", nickName);
        } finally {
            this.persistence.readUnlock();
        }
    }
    
    
    
    public synchronized void traceNickChange(IrcUser oldUser, IrcUser newUser) {
        if (!this.checkAttribute(oldUser.getNickName(), 
                MyPlugin.REMIND_TRACK_NICKCHANGE)) {
            logger.trace("Ignoring nickchange for " + oldUser);
            return;
        }
        
        List<RemindEntity> reminds = this.getRemindsForUser(oldUser.getNickName());
        if (reminds.isEmpty()) {
            return;
        }
        
        try {
            logger.debug("Tracing Nickchange: " + oldUser + " to " + newUser);
            
            this.persistence.writeLock();
            
            RemindEntity rmd = this.sleeps.get(oldUser.getNickName());
            this.sleeps.put(oldUser.getNickName(), null);
            this.sleeps.put(newUser.getNickName(), rmd);
            
            this.persistence.startTransaction();
            for (RemindEntity remind : reminds) {
                logger.trace("Setting receiver for remind " + remind.getId() + 
                        " from " + oldUser + " to " + newUser);
                remind.setForUser(newUser.getNickName());
            }
            this.persistence.commitTransaction();
        } catch (DatabaseException e) {
            e.printStackTrace();
        } finally {
            this.persistence.writeUnlock();
        }
    }


    
    @Override
    protected void actualDispose() throws DisposingException {
        logger.debug("Shutting down remind scheduler.");
        this.remindScheduler.cancel();
    }
}