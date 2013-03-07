package de.skuzzle.polly.sdk.eventlistener;

import de.skuzzle.polly.sdk.IrcManager;

/**
 * This class represents a message event which may have occurred on a channel or a
 * query with a user. It holds information about the user who caused this event and
 * the message he sent.
 *  
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public class MessageEvent extends ChannelEvent {

    private String message;
    
    
    /**
     * Creates a new MessageEvent.
     * @param source The source {@link IrcManager}.
     * @param user The user who caused this event.
     * @param channel The channel on which this event occurred.
     * @param message The message that was sent to the channel/query
     */
    public MessageEvent(IrcManager source, IrcUser user, 
            String channel, String message) {
        super(source, user, channel);
        this.message = message;
    }
    
    
    
    /**
     * Gets the message that was sent to the channel.
     * @return The message.
     */
    public String getMessage() {
        return this.message;
    }
    
    
    
    /**
     * Formats this event to a suitable String.
     * @return A String representation of this event.
     */
    @Override
    public String toString() {
        return super.toString() + ": " + this.message;
    }
}
