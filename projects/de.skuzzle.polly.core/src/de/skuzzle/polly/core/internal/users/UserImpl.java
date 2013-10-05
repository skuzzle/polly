package de.skuzzle.polly.core.internal.users;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;





import de.skuzzle.polly.core.internal.roles.Role;
import de.skuzzle.polly.core.util.Hashes;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.UnknownAttributeException;
import de.skuzzle.polly.sdk.time.Time;


@Entity(name = "User")
@NamedQueries({
    @NamedQuery(
        name  = "USER_BY_NAME", 
        query = "SELECT u FROM User u WHERE u.name=?1"),
    @NamedQuery(
        name  = "ALL_USERS", 
        query = "SELECT u FROM User u")
})
public class UserImpl implements de.skuzzle.polly.sdk.User, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public final static String ALL_USERS = "ALL_USERS";
    
   
    @Id@GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    
    
    // XXX: this field is outdated and exists only for database compatabilty
    @SuppressWarnings("unused")
    private int userLevel;
    
    @Column(unique = true, columnDefinition = "VARCHAR(255)")
    private String name;
    
    @Column(columnDefinition = "VARCHAR(32)")
    private String password;
    
    @OneToMany
    private Set<Role> roles;
    
    @ElementCollection(targetClass=String.class)
    @ManyToMany(cascade = CascadeType.ALL)
    private Map<String, String> attributes;
    
    @Transient
    private String currentNickName;
    
    @Transient
    private long lastActionTimeStamp;
    
    @Transient
    private long loginTime;
    
    @Transient
    private long lastIdleTimeStamp;
    
    @Transient
    private boolean isPollyAdmin;
    
    @Transient
    private UserManagerImpl userManager;
    
    UserImpl() {
        this("", "");
    }
    
    
    
    UserImpl(String name, String password) {
        this.name = name;
        this.password = Hashes.md5(password);
        this.attributes = new HashMap<String, String>();
        this.lastActionTimeStamp = Time.currentTimeMillis();
        this.roles = new HashSet<Role>();
        this.lastIdleTimeStamp = Time.currentTimeMillis();
    }
    
    
    
    public UserImpl setUserManager(UserManagerImpl userManager) {
        this.userManager = userManager;
        return this;
    }
    
    
    public int getId() {
        return this.id;
    }
    
    
    
    public Set<Role> getRoles() {
        return this.roles;
    }
    
    

    @Override
    public String getCurrentNickName() {
        return this.currentNickName;
    }


    
    @Override
    public boolean checkPassword(String password) {
        return this.password.equals(Hashes.md5(password));
    }
    
    

    @Override
    public void setPassword(String password) {
        this.password = Hashes.md5(password);
    }
    
    

    @Override
    public String getName() {
        return this.name;
    }

    
    
    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setCurrentNickName(String nickName) {
        this.currentNickName = nickName;
    }


    @Override
    public String getHashedPassword() {
        return this.password;
    }
    
    
    @Override
    public void setHashedPassword(String password) {
        this.password = password;
    }
    
    
    
    @Override
    public String toString() {
        return this.name;
    }

    
    
    public Map<String, String> getAttributes() {
        return this.attributes;
    }
    
    
    
    @Override
    public Set<String> getAttributeNames() {
        return Collections.unmodifiableSet(this.attributes.keySet());
    }


    
    @Override
    public Types getAttribute(String name) {
        synchronized (this.attributes) {
            if (!this.attributes.containsKey(name)) {
                throw new UnknownAttributeException(name);
            }
            return this.userManager.parseValue(null, this.attributes.get(name));
        }
    }
    
    
    
    void setAttribute(String name, Types value) {
        synchronized (this.attributes) {
            if (!this.attributes.containsKey(name)) {
                throw new UnknownAttributeException(name);
            }
            this.attributes.put(name, value.valueString(
                    this.userManager.getPersistenceFormatter()));
        }
    }
    
    
    
    @Override
    public void setLastMessageTime(long timeStamp) {
        this.lastIdleTimeStamp = this.lastActionTimeStamp;
        this.lastActionTimeStamp = timeStamp;
    }
    
    
    
    @Override
    public long getLastMessageTime() {
        return this.lastActionTimeStamp;
    }
    
    
    
    @Override
    public long getLastIdleTime() {
        return this.lastIdleTimeStamp;
    }

    
    
    @Override
    public boolean isPollyAdmin() {
        return this.isPollyAdmin;
    }
    
    
    
    public void setIsPollyAdmin(boolean isAdmin) {
        this.isPollyAdmin = isAdmin;
    }
    
    
    @Override
    public boolean isIdle() {
        return Time.currentTimeMillis() - this.lastActionTimeStamp > IDLE_AFTER;
    }
    
    
    
    @Override
    public long getLoginTime() {
        return this.loginTime;
    }

    
    
    public void setLoginTime(long timeStamp) {
        this.loginTime = timeStamp;
    }
    
    
    
    @Override
    public int compareTo(de.skuzzle.polly.sdk.User o) {
        return o.getName().compareTo(this.getName());
    }
}
