package de.skuzzle.polly.sdk.eventlistener;

import java.util.EventListener;


/**
 * This listener listens for user spotted and user lost events. Those are fired when a
 * user is first seen by polly or leaves pollys sight.
 * 
 * @author Simon
 * @since 0.6.0
 */
public interface UserSpottedListener extends EventListener {

    /**
     * This event is fired when a user comes into pollys sight the first time (= he joins
     * a common channel). This event will first be fired for the same user if he leaves
     * pollys sight in between.
     * 
     * @param e Contains detailed information about this event.
     */
    public abstract void userSpotted(SpotEvent e);
    
    
    
    /**
     * This event is fired when a user leaves pollys sight (= he parts the last common
     * channel or quit).
     * 
     * @param e Contains detailed information about this event.
     */
    public abstract void userLost(SpotEvent e);
}