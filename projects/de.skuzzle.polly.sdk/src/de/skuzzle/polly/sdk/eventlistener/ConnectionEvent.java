package de.skuzzle.polly.sdk.eventlistener;

import java.util.EventObject;

import de.skuzzle.polly.sdk.IrcManager;

/**
 * This event represents a connection status changed event and contains information
 * whether a lost connection is due to a regular quit or was unexpected.
 * 
 * @author Simon
 * @since 0.6.1
 */
public class ConnectionEvent extends EventObject {

    private static final long serialVersionUID = 1L;
    
    private boolean regularLost;
    
    
    /**
     * Creates a new ConnectionEvent for the given source. This should always be an
     * instance of {@link IrcManager}
     * 
     * @param source The source of this event.
     */
    public ConnectionEvent(Object source) {
        this(source, false);
    }

    
    
    /**
     * Creates a new ConnectionEvent for the given source. This should always be an
     * instance of {@link IrcManager}
     * 
     * @param source The source of this event.
     * @param regularLost <code>true</code> if the connection got lost due to a regular
     *          shutdown.
     */
    public ConnectionEvent(Object source, boolean regularLost) {
        super(source);
        this.regularLost = regularLost;
    }
    
    
    
    /**
     * Determines whether the connection has been lost due to a regular shutdown.
     * 
     * @return <code>true</code> if it was a regular connection shutdown.
     */
    public boolean isRegularLost() {
        return this.regularLost;
    }
}
