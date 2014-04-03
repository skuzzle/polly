package de.skuzzle.polly.sdk.eventlistener;

/**
 * This listener listens for join- and part events.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public interface JoinPartListener extends IrcEventListener {
    
	/**
	 * This method is called if a user joins a channel. Detailed information about
	 * this event are provided in the parameter.
	 * @param e The ChannelEvent which holds detailed information about this event.
	 */
    public void channelJoined(ChannelEvent e);
    
    
    
	/**
	 * This method is called if a user leaves a channel. Detailed information about
	 * this event are provided in the parameter.
	 * @param e The ChannelEvent which holds detailed information about this event.
	 */
    public void channelParted(ChannelEvent e);
}
