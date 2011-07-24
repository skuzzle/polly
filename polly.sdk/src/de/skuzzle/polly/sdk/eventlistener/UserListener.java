package de.skuzzle.polly.sdk.eventlistener;

// TODO: comment
public interface UserListener {

    public abstract void userSignedOn(UserEvent e);
    
    public abstract void userSignedOff(UserEvent e);
    
}