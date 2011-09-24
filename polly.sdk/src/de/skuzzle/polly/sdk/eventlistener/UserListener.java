package de.skuzzle.polly.sdk.eventlistener;

import java.util.EventListener;

// TODO: comment
public interface UserListener extends EventListener {

    public abstract void userSignedOn(UserEvent e);
    
    public abstract void userSignedOff(UserEvent e);
    
}