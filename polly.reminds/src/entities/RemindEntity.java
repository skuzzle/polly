package entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.skuzzle.polly.sdk.FormatManager;

@Entity
@NamedQueries({
    @NamedQuery(
        name =  "ALL_REMINDS",
        query = "SELECT r FROM RemindEntity r"),
    @NamedQuery(
        name =  "REMIND_FOR_USER",
        query = "SELECT r FROM RemindEntity r WHERE r.forUser = ?1"),
    @NamedQuery(
        name =  "UNDELIVERED_FOR_USER",
        query = "SELECT r FROM RemindEntity r WHERE r.forUser = ?1 AND " +
        		"r.isMessage = true")
})
public class RemindEntity {

    @Id@GeneratedValue
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
    
    private boolean remind;
    
    
    
    public RemindEntity() {}
    
    
    
    public RemindEntity(String message, String fromUser, String forUser, 
            String onChannel, Date dueDate) {
        this.message = message;
        this.fromUser = fromUser;
        this.forUser = forUser;
        this.dueDate = dueDate;
        this.onChannel = onChannel;
        this.leaveDate = new Date();
    }


    public int getId() {
        return id;
    }


    public String getMessage() {
        return this.message;
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
    
    
    
    public Date getLeaveDate() {
        return this.leaveDate;
    }
    
    
    
    public boolean isMessage() {
        return this.isMessage;
    }
    
    
    
    public void setIsMessage(boolean isMessage) {
        this.isMessage = isMessage;
    }
    
    
    
    public boolean wasRemind() {
        return this.remind;
    }
    
    
    
    public void setWasRemind(boolean wasRemind) {
        this.remind = wasRemind;
    }
    
    
    
    public String toString(FormatManager formatter) {
        return "(" + this.id + ") Erinnerung von " + this.fromUser + " an " + 
            this.message + " (" + formatter.formatDate(this.dueDate) + ")";
    }
    
    
    
    public RemindEntity copyForNewDueDate(Date newDueDate) {
        RemindEntity copy = new RemindEntity(this.message, this.fromUser, this.forUser, 
                this.onChannel, newDueDate);
        copy.leaveDate = this.leaveDate;
        return copy;
    }
}
