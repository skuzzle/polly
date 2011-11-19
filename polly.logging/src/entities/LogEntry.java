package entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class LogEntry {
    
    public final static int TYPE_MESSAGE = 0;
    public final static int TYPE_JOIN = 1;
    public final static int TYPE_PART = 2;
    public final static int TYPE_QUIT = 4;
    public final static int TYPE_NICKCHANGE = 8;
    
    
    public static LogEntry forMessage(String user, String message, String channel, 
            Date date) {
        return new LogEntry(user, message, channel, date, TYPE_MESSAGE);
    }
    
    public static LogEntry forJoin(String user, String message, String channel, 
            Date date) {
        return new LogEntry(user, message, channel, date, TYPE_JOIN);
    }
    
    public static LogEntry forPart(String user, String message, String channel, 
            Date date) {
        return new LogEntry(user, message, channel, date, TYPE_PART);
    }
    
    public static LogEntry forQuit(String user, String message, String channel, 
            Date date) {
        return new LogEntry(user, message, channel, date, TYPE_QUIT);
    }

    public static LogEntry forNickChange(String user, String message, String channel, 
            Date date) {
        return new LogEntry(user, message, channel, date, TYPE_NICKCHANGE);
    }
    
    
    @Id@GeneratedValue
    private int id;
    
    private String user;
    
    private String message;
    
    private String channel;
    
    private Date date;
    
    private int type;

    
    public LogEntry() {}
    
    private LogEntry(String user, String message, String channel, Date date, int type) {
        super();
        this.user = user;
        this.message = message;
        this.channel = channel;
        this.date = date;
        this.type = type;
    }

    
    public int getId() {
        return id;
    }

    
    public String getUser() {
        return user;
    }

    
    public String getMessage() {
        return message;
    }

    
    public String getChannel() {
        return channel;
    }

    
    public Date getDate() {
        return date;
    }
    
    
    
    public int getType() {
        return type;
    }
}
