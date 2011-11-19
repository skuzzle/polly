package entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;



@Entity
@NamedQueries({
    @NamedQuery(
        name = "LOGS_FOR_USER",
        query = "SELECT l FROM LogEntry l WHERE LOWER(l.nickname) LIKE LOWER(?1) ORDER BY l.date ASC"
    ),
    @NamedQuery(
        name = "USER_SEEN",
        query = "SELECT l FROM LogEntry l WHERE l.nickname = ?1 ORDER BY l.date DESC"
    )
})
public class LogEntry {
    
    public final static int TYPE_MESSAGE = 0;
    public final static int TYPE_JOIN = 1;
    public final static int TYPE_PART = 2;
    public final static int TYPE_QUIT = 4;
    public final static int TYPE_NICKCHANGE = 8;
    
    public final static String FIND_BY_USER = "LOGS_FOR_USER";
    public final static String FIND_BY_CHANNEL = "FIND_BY_CHANNEL";
    public final static String USER_SSEN = "USER_SEEN";
    
    
    
    public static LogEntry forMessage(String nickname, String message, String channel, 
            Date date) {
        return new LogEntry(nickname, message, channel, date, TYPE_MESSAGE);
    }
    
    public static LogEntry forJoin(String nickname, String message, String channel, 
            Date date) {
        return new LogEntry(nickname, message, channel, date, TYPE_JOIN);
    }
    
    public static LogEntry forPart(String nickname, String message, String channel, 
            Date date) {
        return new LogEntry(nickname, message, channel, date, TYPE_PART);
    }
    
    public static LogEntry forQuit(String nickname, String message, String channel, 
            Date date) {
        return new LogEntry(nickname, message, channel, date, TYPE_QUIT);
    }

    public static LogEntry forNickChange(String nickname, String message, String channel, 
            Date date) {
        return new LogEntry(nickname, message, channel, date, TYPE_NICKCHANGE);
    }
    
    
    @Id@GeneratedValue
    private int id;
    
    private String nickname;
    
    private String message;
    
    private String channel;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    
    private int type;

    
    public LogEntry() {}
    
    private LogEntry(String nickname, String message, String channel, Date date, int type) {
        super();
        this.nickname = nickname;
        this.message = message;
        this.channel = channel;
        this.date = date;
        this.type = type;
    }

    
    public int getId() {
        return id;
    }

    
    public String getNickname() {
        return nickname;
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
