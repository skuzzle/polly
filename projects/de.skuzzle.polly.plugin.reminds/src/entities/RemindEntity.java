package entities;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.skuzzle.polly.sdk.time.Time;

@Entity
@NamedQueries({
    @NamedQuery(
        name =  RemindEntity.ALL_REMINDS,
        query = "SELECT r FROM RemindEntity r"),
    @NamedQuery(
        name =  RemindEntity.REMIND_FOR_USER,
        query = "SELECT r FROM RemindEntity r WHERE r.forUser = ?1"),
    @NamedQuery(
        name =  RemindEntity.MY_REMIND_FOR_USER,
        query = "SELECT r FROM RemindEntity r WHERE r.forUser = ?1 OR r.fromUser = ?1"),
    @NamedQuery(
        name =  RemindEntity.UNDELIVERED_FOR_USER,
        query = "SELECT r FROM RemindEntity r WHERE r.forUser = ?1 AND " +
        		"r.isMessage = true")
})
public class RemindEntity implements Comparable<RemindEntity> {
    
    public final static String ALL_REMINDS = "ALL_REMINDS"; //$NON-NLS-1$
    public final static String REMIND_FOR_USER = "REMIND_FOR_USER"; //$NON-NLS-1$
    public final static String MY_REMIND_FOR_USER = "MY_REMIND_FOR_USER"; //$NON-NLS-1$
    public final static String UNDELIVERED_FOR_USER = "UNDELIVERED_FOR_USER"; //$NON-NLS-1$

    
    public static final Comparator<RemindEntity> BY_DUE_DATE = new Comparator<RemindEntity>() {

        @Override
        public int compare(RemindEntity re1, RemindEntity re2) {
            return re1.getDueDate().compareTo(re2.getDueDate());
        }
    };

    @Id@GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    
    @Column(columnDefinition = "VARCHAR(255)")
    private String message;
    
    @Column(columnDefinition = "VARCHAR(255)")
    private String forUser;
    
    @Column(columnDefinition = "VARCHAR(255)")
    private String fromUser;
    
    @Column(columnDefinition = "VARCHAR(255)")
    private String onChannel;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date leaveDate;
    
    private boolean isMessage;
    
    private boolean isMail;
    
    private boolean remind;
    
    private transient boolean onAction;
    
    public RemindEntity() {}
    
    
    
    public RemindEntity(String message, String fromUser, String forUser, 
            String onChannel, Date dueDate, Date leaveDate) {
        this(message, fromUser, forUser, onChannel, dueDate, false, false, leaveDate);
    }
    
    public RemindEntity(String message, String fromUser, String forUser, 
        String onChannel, Date dueDate, boolean onAction, Date leaveDate) {
        this(message, fromUser, forUser, onChannel, dueDate, onAction, false, leaveDate);
    }
    
    
    public RemindEntity(String message, String fromUser, String forUser, 
            String onChannel, Date dueDate, boolean onAction, boolean isMail, 
            Date leaveDate) {
        this.message = message;
        this.fromUser = fromUser;
        this.forUser = forUser;
        this.dueDate = dueDate;
        this.onChannel = onChannel;
        this.leaveDate = leaveDate;
        this.onAction = onAction;
        this.isMail = isMail;
    }


    public int getId() {
        return id;
    }


    public String getMessage() {
        return this.message;
    }

    
    public void setMessage(String message) {
        this.message = message;
    }
    

    public String getForUser() {
        return this.forUser;
    }
    
    
    
    public void setForUser(String forUser) {
        this.forUser = forUser;
    }
    
    
    
    public String getFromUser() {
        return this.fromUser;
    }
    
    
    
    public String getOnChannel() {
        return this.onChannel;
    }


    public Date getDueDate() {
        return dueDate;
    }
    
    
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    
    
    public Date getLeaveDate() {
        return this.leaveDate;
    }
    
    
    
    public boolean isMessage() {
        return this.isMessage;
    }
    
    
    
    public void setIsMessage(boolean isMessage) {
        this.isMessage = isMessage;
    }
    
    
    
    public boolean isMail() {
        return this.isMail;
    }
    
    
    
    public void setIsMail(boolean isMail) {
        this.isMail = isMail;
    }
    
    
    
    public boolean wasRemind() {
        return this.remind;
    }
    
    
    
    public void setWasRemind(boolean wasRemind) {
        this.remind = wasRemind;
    }
    
    
    
    public boolean isOnAction() {
        return this.onAction;
    }
    
    
    
    public void setOnAction(boolean onAction) {
        this.onAction = onAction;
    }
    
    
    
    public RemindEntity copyForNewDueDate(Date newDueDate) {
        RemindEntity copy = new RemindEntity(this.message, this.fromUser, this.forUser, 
                this.onChannel, newDueDate, this.leaveDate);
        copy.leaveDate = Time.currentTime();
        return copy;
    }
    
    
    
    @Override
    public String toString() {
        return "REMIND " + this.id + " for " + this.getForUser(); //$NON-NLS-1$ //$NON-NLS-2$
    }



    @Override
    public int compareTo(RemindEntity o) {
        return this.dueDate.compareTo(o.dueDate);
    }
}
