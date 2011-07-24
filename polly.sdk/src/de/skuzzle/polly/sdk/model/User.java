package de.skuzzle.polly.sdk.model;


public interface User {

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
    
    public abstract String getAttribute(String name);
    
    public abstract void setAttribute(String name, String value);
}
