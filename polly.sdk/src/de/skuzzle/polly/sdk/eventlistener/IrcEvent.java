package de.skuzzle.polly.sdk.eventlistener;

import de.skuzzle.polly.sdk.IrcManager;

/**
 * Base event class for all irc events.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public class IrcEvent {

    private IrcManager source;
    
    
    /**
     * Creates a new irc event.
     * @param source The source IrcManager.
     */
    public IrcEvent(IrcManager source) {
        this.source = source;
    }
    
    
    
    /**
     * Gets the source {@link IrcManager}.
     * @return The IrcManager.
     */
    public IrcManager getSource() {
        return this.source;
    }
}
