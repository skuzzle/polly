package polly.data;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;


import de.skuzzle.polly.sdk.exceptions.UnknownAttributeException;


@Entity
@NamedQueries({
    @NamedQuery(
        name  = "USER_BY_NAME", 
        query = "SELECT u FROM User u WHERE u.name=?1"),
    @NamedQuery(
        name  = "ALL_USERS", 
        query = "SELECT u FROM User u")
})
public class User implements de.skuzzle.polly.sdk.model.User, Serializable {
    
    private static final long serialVersionUID = 1L;
   
    @Id@GeneratedValue
    private int id;
    
    private int userLevel;
    
    @Column(unique = true, columnDefinition = "VARCHAR(255)")
    private String name;
    
    @Column(columnDefinition = "VARCHAR(32)")
    private String password;
    
    @ElementCollection(targetClass=String.class)
    @ManyToMany(cascade = CascadeType.ALL)
    private Map<String, String> attributes;
    
    @Transient
    private String currentNickName;    

    
    public User() {}
    
    
    
    public User(String name, String password, int userLevel) {
        this.name = name;
        this.password = User.createMD5Hash(password);
        this.userLevel = userLevel;
        this.attributes = new HashMap<String, String>();
    }
    
    
    
    public int getId() {
        return this.id;
    }
    
    

    @Override
    public int getUserLevel() {
        return this.userLevel;
    }
    
    

    @Override
    public String getCurrentNickName() {
        return this.currentNickName;
    }


    
    @Override
    public boolean checkPassword(String password) {
        return this.password.equals(User.createMD5Hash(password));
    }
    
    

    @Override
    public void setPassword(String password) {
        this.password = User.createMD5Hash(password);
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
    public void setUserLevel(int level) {
        this.userLevel = level;
    }
    
    
    @Override
    public String toString() {
        return this.name + "(Level " + this.userLevel + ")";
    }
    
    
    
    private static String createMD5Hash(String string) {
        String result = "";
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(string.getBytes(), 0, string.length());
            result = new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    
    
    public Map<String, String> getAttributes() {
        return this.attributes;
    }


    
    @Override
    public String getAttribute(String name) {
        if (!this.attributes.containsKey(name)) {
            throw new UnknownAttributeException(name);
        }
        return this.attributes.get(name);
    }
    
    
    
    @Override
    public void setAttribute(String name, String value) {
        if (!this.attributes.containsKey(name)) {
            throw new UnknownAttributeException(name);
        }
        this.attributes.put(name, value);
    }
}
