package de.skuzzle.polly.sdk.eventlistener;

import java.util.EventListener;

import de.skuzzle.polly.tools.events.Dispatch;

/**
 * Listener which listens for sign on/sign off events.
 * 
 * @author Simon
 * @since 0.6
 */
public interface UserListener extends EventListener {
    
    public final static Dispatch<UserListener, UserEvent> SIGNED_ON = 
            new Dispatch<UserListener, UserEvent>() {
        @Override
        public void dispatch(UserListener listener, UserEvent event) {
            listener.userSignedOn(event);
        }
    };
    
    public final static Dispatch<UserListener, UserEvent> SIGNED_OFF = 
            new Dispatch<UserListener, UserEvent>() {
        @Override
        public void dispatch(UserListener listener, UserEvent event) {
            listener.userSignedOff(event);
        }
    };
    
    
    
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