package de.skuzzle.polly.sdk.eventlistener;

import java.util.EventListener;

/**
 * Listener which listens for sign on/sign off events.
 * 
 * @author Simon
 * @ since 0.6
 */
public interface UserListener extends EventListener {

    /**
     * This event is raised when a user signed on.
     * 
     * @param e Contains detailed information about this event.
     */
    public abstract void userSignedOn(UserEvent e);
    
    /**
     * This event is raised when a user signed off.
     * 
     * @param e Contains detailed information about this event.
     */
    public abstract void userSignedOff(UserEvent e);
    
}