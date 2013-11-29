package de.skuzzle.polly.tools.events;

import java.util.EventListener;

/**
 * This is kind of a tagging interface for event listener which will only be
 * notified once. After being notified, the listener is removed from the 
 * {@link EventProvider} it was registered at.
 * @author Simon
 */
public interface OneTimeEventListener extends EventListener {
    
    /**
     * This method specifies whether this listner's work is done and it should be 
     * removed from its parent's {@link EventProvider} after the next time the listener
     * was notified.
     * @return Whether to remove this listener from its parent after next notification.
     */
    public boolean workDone();
}
