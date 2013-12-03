package polly.dyndns.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import polly.dyndns.MSG;
import de.skuzzle.polly.sdk.time.Time;

@Entity
@NamedQueries({
    @NamedQuery(
        name = Account.QUERY_ACCOUNTS_BY_HOSTER,
        query= "SELECT a FROM Account a WHERE a.hoster.id = ?1"
    ),
    @NamedQuery(
        name = Account.QUERY_ALL_ACCOUNTS, 
        query = "SELECT a FROM Account a"
    )
})
public class Account {

    public static final String QUERY_ACCOUNTS_BY_HOSTER = "ACCOUNTS_BY_HOSTER"; //$NON-NLS-1$
    public static final String QUERY_ALL_ACCOUNTS = "ALL_ACCOUNTS"; //$NON-NLS-1$

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    private String userName;
    private String domainName;
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    private Hoster hoster;

    private transient String currentStatus;
    private transient long updateTime;



    public Account() {
    }



    public Account(Hoster hoster, String userName, String domainName, String password) {
        this.hoster = hoster;
        this.userName = userName;
        this.domainName = domainName;
        this.password = password;
        this.updateTime = Time.currentTimeMillis();
        this.currentStatus = MSG.none;
    }



    public String getUserName() {
        return this.userName;
    }



    public void setUserName(String userName) {
        this.userName = userName;
    }



    public String getDomainName() {
        return this.domainName;
    }



    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }



    public String getPassword() {
        return this.password;
    }



    public void setPassword(String password) {
        this.password = password;
    }



    public Hoster getHoster() {
        return this.hoster;
    }



    public void setHoster(Hoster hoster) {
        this.hoster = hoster;
    }



    public int getId() {
        return this.id;
    }



    public String getCurrentStatus() {
        return this.currentStatus;
    }



    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }



    public long getUpdateTime() {
        return this.updateTime;
    }



    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
