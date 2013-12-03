package polly.dyndns.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;


@Entity
@NamedQuery(name = Hoster.QUERY_ALL_HOSTERS, query = "SELECT h FROM Hoster h")
public class Hoster {
    
    public final static String QUERY_ALL_HOSTERS = "ALL_HOSTERS";
    
    @Id@GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    private String name;
    private String userName;
    private String hostName;
    private String password;
    protected String apiUrl;
    
    private transient String currentStatus;
    private transient long updateTime;
    
    
    
    public Hoster() {
        this("", "", "", "", "");
    }
    
    
    
    public Hoster(String name, String userName, String hostName, String password, String apiUrl) {
        this.name = name;
        this.userName = userName;
        this.hostName = hostName;
        this.password = password;
        this.apiUrl = apiUrl;
    }
    
    
    
    public int getId() {
        return this.id;
    }
    
    
    
    
    public String getName() {
        return this.name;
    }
    
    
    
    public String getUserName() {
        return this.userName;
    }
    

    
    public String getHostName() {
        return this.hostName;
    }
    
    
    
    public String getPassword() {
        return this.password;
    }
    
    
    
    public String getApiUrl() {
        return this.apiUrl;
    }
    
    
    
    
    public String getCurrentStatus() {
        return this.currentStatus;
    }
    
    
    
    public long getUpdateTime() {
        return this.updateTime;
    }
    
    
    
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
    
    
    
    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
