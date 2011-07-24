package de.skuzzle.polly.sdk.eventlistener;

import de.skuzzle.polly.sdk.IrcManager;

/**
 * This class represents an irc channel event. It holds the source IrcManager, the user
 * who caused this event and the channel in which this event occurred.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public class ChannelEvent extends IrcEvent {

    IrcUser user;
    String channel;
    
    
    /**
     * Creates a new ChannelEvent.
     * @param source The IrcManager.
     * @param user The user who caused this event.
     * @param channel The channel in which this event occurred.
     */
    public ChannelEvent(IrcManager source, IrcUser user, String channel) {
        super(source);
        this.user = user;
        this.channel = channel;
    }
    
    
    
    /**
     * gets the user who caused this event.
     * @return The user.
     */
    public IrcUser getUser() {
        return this.user;
    }
    
    
    
    /**
     * Gets the channel in which this event occurred.
     * @return The channel.
     */
    public String getChannel() {
        return this.channel;
    }
    
    
    
    /**
     * Determines if this event occurred on a query with a user.
     * @return <code>true</code> iff this event occurred on a query.
     */
    public boolean inQuery() {
        // TODO: strip '#' off channel name?
        return this.channel.equalsIgnoreCase(this.user.getNickName());
    }
    
    
    
    /**
     * Formats this event to a suitable string.
     * 
     * @return A String representation of this event. 
     */
    @Override
    public String toString() {
    	if (this.inQuery()) {
    		return "*** QUERY " + this.user;
    	}
        return this.channel + " " + this.user;
    }

}
