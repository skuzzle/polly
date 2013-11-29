package de.skuzzle.polly.sdk.eventlistener;

import java.util.EventListener;

import de.skuzzle.polly.tools.events.Dispatch;

/**
 * Listener for irc connection state changes.
 * 
 * @author Simon
 * @since 0.6.1
 */
public interface ConnectionListener extends EventListener {

    public final static Dispatch<ConnectionListener, ConnectionEvent> CONNECTION_ESTABLISHED = 
            new Dispatch<ConnectionListener, ConnectionEvent>() {
        @Override
        public void dispatch(ConnectionListener listener, ConnectionEvent event) {
            listener.ircConnectionEstablished(event);
        }
    };
    
    
    
    public final static Dispatch<ConnectionListener, ConnectionEvent> CONNECTION_LOST = 
            new Dispatch<ConnectionListener, ConnectionEvent>() {
        @Override
        public void dispatch(ConnectionListener listener, ConnectionEvent event) {
            listener.ircConnectionLost(event);
        }
    };
    
    
    
    /**
     * This event is fired whenever a new irc connection is established (on startup or
     * due to reconnects).
     * 
     * @param e Contains detailed information about the event.
     */
    public void ircConnectionEstablished(ConnectionEvent e);
    
    
    
    /**
     * This event is fired whenever the irc connection is lost. 
     * 
     * @param e Contains detailed information about the event.
     */
    public void ircConnectionLost(ConnectionEvent e);
}
