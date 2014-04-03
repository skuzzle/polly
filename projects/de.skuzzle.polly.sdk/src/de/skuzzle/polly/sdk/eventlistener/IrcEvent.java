package de.skuzzle.polly.sdk.eventlistener;

import java.util.Date;

import de.skuzzle.jeve.Event;
import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.time.Time;

/**
 * Base event class for all irc events.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public class IrcEvent extends Event<IrcManager> {

    private Date date;
    
    
    
    /**
     * Creates a new irc event.
     * @param source The source IrcManager.
     */
    public IrcEvent(IrcManager source) {
        super(source);
        this.date = Time.currentTime();
    }
    
    
    
    /**
     * Gets the date of when this event has been created.
     * 
     * @return Event date.
     * @since 0.9.1
     */
    public Date getDate() {
        return this.date;
    }
}
