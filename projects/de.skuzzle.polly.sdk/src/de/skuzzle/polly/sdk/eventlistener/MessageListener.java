package de.skuzzle.polly.sdk.eventlistener;

/**
 * This listener listens for message events on any channel.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public interface MessageListener extends IrcEventListener {
    
	/**
	 * This method is called whenever a message is send to a channel. Detailed 
	 * information about the message is contained within the {@link MessageEvent}.
	 * @param e The MessageEvent which holds detailed information about this event.
	 */
    public void publicMessage(MessageEvent e);

    
    
	/**
	 * This method is called whenever a message is send to a query. Detailed 
	 * information about the message is contained within the {@link MessageEvent}.
	 * @param e The MessageEvent which holds detailed information about this event.
	 */
    public void privateMessage(MessageEvent e);
    
    
    
	/**
	 * This method is called whenever a user sends an action to a channel. That is, he
	 * sent something like <code>/me ...</code> on most clients.
	 * @param e The MessageEvent which holds detailed information about this event.
	 */
    public void actionMessage(MessageEvent e);
    
    
    /**
     * This method is called whenever we receive a notice.
     * 
     * @param e The MessageEvent which holds detailed information about this event.
     */
    public void noticeMessage(MessageEvent e);
}
