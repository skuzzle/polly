package de.skuzzle.polly.sdk.eventlistener;

import java.util.EventListener;

import de.skuzzle.polly.tools.events.Dispatch;


/**
 * This listener listens for user spotted and user lost events. Those are fired when a
 * user is first seen by polly or leaves pollys sight.
 * 
 * @author Simon
 * @since 0.6.0
 */
public interface UserSpottedListener extends EventListener {
    
    public final static Dispatch<UserSpottedListener, SpotEvent> USER_SPOTTED = 
            new Dispatch<UserSpottedListener, SpotEvent>() {
        @Override
        public void dispatch(UserSpottedListener listener, SpotEvent event) {
            listener.userSpotted(event);
        }
    };
    
    public final static Dispatch<UserSpottedListener, SpotEvent> USER_LOST = 
            new Dispatch<UserSpottedListener, SpotEvent>() {
        @Override
        public void dispatch(UserSpottedListener listener, SpotEvent event) {
            listener.userLost(event);
        }
    };
    
    
    
    /**
     * This event is fired when a user comes into pollys sight the first time (= he joins
     * a common channel). This event will first be fired for the same user if he leaves
     * polly's sight in between.
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