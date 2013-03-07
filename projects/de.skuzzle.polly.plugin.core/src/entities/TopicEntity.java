package entities;

import java.util.Calendar;
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


@Entity
@NamedQueries({
    @NamedQuery(
        name =  "ALL_TOPICS",
        query = "SELECT t FROM TopicEntity t"),
    @NamedQuery(
        name =  "TOPICS_FOR_USER",
        query = "SELECT t FROM TopicEntity t WHERE t.fromUser = ?1")
})
public class TopicEntity {

    @Id@GeneratedValue(strategy = GenerationType.TABLE)
    private int id;

    @Column(columnDefinition = "VARCHAR (255)")
    private String channel;
    
    @Column(columnDefinition = "VARCHAR (255)")
    private String pattern;
    
    @Column(columnDefinition = "VARCHAR (255)")
    private String after;
    
    @Column(columnDefinition = "VARCHAR (255)")
    private String fromUser;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;
    
    
    
    public TopicEntity() {}



    public TopicEntity(String fromUser, String channel, String pattern, String after, 
            Date dueDate) {
        super();
        this.fromUser = fromUser;
        this.channel = channel;
        this.pattern = pattern;
        this.after = after;
        this.dueDate = dueDate;
    }



    public int getId() {
        return id;
    }

    
    
    public String getFromUser() {
        return this.fromUser;
    }
    
    
    public String getChannel() {
        return this.channel;
    }



    public String getPattern() {
        return pattern;
    }



    public String getAfter() {
        return after;
    }



    public Date getDueDate() {
        return dueDate;
    }
    
    
    
    public long remainingDays() {
        Calendar now = Calendar.getInstance();
        Calendar due = Calendar.getInstance();
        due.setTime(this.dueDate);
        
        long diff = due.getTimeInMillis() - now.getTimeInMillis();
        return diff / (24L * 60L *60L * 1000L);
    }
    
    
    
    public boolean isDue() {
        return this.remainingDays() == 0;
    }
}