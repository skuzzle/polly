package de.skuzzle.polly.sdk.eventlistener;

import de.skuzzle.polly.sdk.IrcManager;


/**
 * This class extends a normal {@link MessageEvent} to add a field 
 * {@link #getMessageSource()} which returns the component from which this message has
 * been sent.
 *  
 * @author Simon
 * @since 0.7
 */
public class OwnMessageEvent extends MessageEvent {

    private Object messageSource;
    
    
    
    /**
     * Creates a new MessageEvent.
     * @param source The source {@link IrcManager}.
     * @param user The user who caused this event.
     * @param channel The channel on which this event occurred.
     * @param message The message that was sent to the channel/query
     * @param messageSource The Object from which this message has been sent.
     */
    public OwnMessageEvent(IrcManager source, IrcUser user, String channel,
        String message, Object messageSource) {
        super(source, user, channel, message);
        this.messageSource = messageSource;
    }



    /**
     * The Object from which this message has been sent.
     * 
     * @return The source Object.
     */
    public Object getMessageSource() {
        return this.messageSource;
    }
}
