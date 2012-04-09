package de.skuzzle.polly.sdk.model;

import java.util.Set;


public interface User extends Comparable<User> {
    
    /**
     * Time in milliseconds after which a user is considered to be idle
     */
    public final static long IDLE_AFTER = 60 * 1000 * 30; // 30 min

    public abstract int getUserLevel();
    
    public abstract void setUserLevel(int level);
    
    public abstract boolean checkPassword(String password);
    
    public abstract String getHashedPassword();
    
    public abstract void setHashedPassword(String password);
    
    public abstract void setPassword(String password);
    
    public abstract String getName();
    
    public abstract void setName(String name);
    
    public abstract String getCurrentNickName();
    
    public abstract void setCurrentNickName(String nickName);
    
    public abstract Set<String> getAttributeNames();
    
    public abstract String getAttribute(String name);
    
    public abstract boolean isIdle();
    
    public abstract long getLoginTime();
}
