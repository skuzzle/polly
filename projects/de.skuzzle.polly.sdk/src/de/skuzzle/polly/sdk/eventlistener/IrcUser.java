package de.skuzzle.polly.sdk.eventlistener;


/**
 * This class represents a plain irc user. That means it holds the raw user data from
 * the irc and no further information.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public class IrcUser {

    private String nickName;
    private String login;
    private String hostName;
    
    
    /**
     * Creates a new irc user.
     * @param nickName The users nickname.
     * @param login The users irc ident.
     * @param hostName the users hostname.
     */
    public IrcUser(String nickName, String login, String hostName) {
        this.nickName = nickName;
        this.login = login;
        this.hostName = hostName;
    }



    /**
     * Gets the users nickname.
     * @return The nickname.
     */
    public String getNickName() {
        return this.nickName;
    }



    /**
     * Gets the users irc ident.
     * @return The ident.
     */
    public String getLogin() {
        return this.login;
    }


    
    /**
     * Gets the users hostname.
     * @return The hostname.
     */
    public String getHostName() {
        return this.hostName;
    }
    
    
    
    /**
     * Formats this user to a suitable string.
     * @return The users nickname.
     */
    @Override
    public String toString() {
        return this.nickName;
    }




    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((this.nickName == null) ? 0 : this.nickName.hashCode());
        return result;
    }


    
    /**
     * Checks if two IrcUser are equal. They are, if theire nicknames equal.
     * 
     * @param obj The object to compare this with.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof IrcUser)) {
            return false;
        }
        IrcUser other = (IrcUser) obj;
        if (this.nickName == null) {
            if (other.nickName != null) {
                return false;
            }
        } else if (!this.nickName.equals(other.nickName)) {
            return false;
        }
        return true;
    }
}
