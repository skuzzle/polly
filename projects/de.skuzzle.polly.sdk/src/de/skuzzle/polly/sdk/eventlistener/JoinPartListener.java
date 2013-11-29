package de.skuzzle.polly.sdk.eventlistener;

import de.skuzzle.polly.tools.events.Dispatch;

/**
 * This listener listens for join- and part events.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public interface JoinPartListener extends IrcEventListener {
    
    public final static Dispatch<JoinPartListener, ChannelEvent> CHANNEL_JOINED = 
            new Dispatch<JoinPartListener, ChannelEvent>() {
        @Override
        public void dispatch(JoinPartListener listener, ChannelEvent event) {
            listener.channelJoined(event);
        }
    };
    
    public final static Dispatch<JoinPartListener, ChannelEvent> CHANNEL_PARTED = 
            new Dispatch<JoinPartListener, ChannelEvent>() {
        @Override
        public void dispatch(JoinPartListener listener, ChannelEvent event) {
            listener.channelParted(event);
        }
    };
    
    
    
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
