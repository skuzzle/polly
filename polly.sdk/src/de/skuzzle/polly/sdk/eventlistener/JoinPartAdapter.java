package de.skuzzle.polly.sdk.eventlistener;

/**
 * This is an adapter class for {@link JoinPartListener}s. It provides empty 
 * implementations of each event method.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public abstract class JoinPartAdapter implements JoinPartListener {

	/**
	 * Empty implementation of {@link JoinPartListener#channelJoined(ChannelEvent)}.
	 * @param e The ChannelEvent which holds detailed information about this event.
	 */
    @Override
    public void channelJoined(ChannelEvent e) {}
    
    
    
	/**
	 * Empty implementation of {@link JoinPartListener#channelParted(ChannelEvent)}.
	 * @param e The ChannelEvent which holds detailed information about this event.
	 */
    @Override
    public void channelParted(ChannelEvent e) {}
}
