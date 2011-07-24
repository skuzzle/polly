package de.skuzzle.polly.sdk.eventlistener;

/**
 * This is an adapter class for {@link MessageListener}s. It provides empty 
 * implementations of each event method.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public abstract class MessageAdapter implements MessageListener {

	/**
	 * Empty implementation of {@link MessageListener#publicMessage(MessageEvent)}.
	 * @param e The MessageEvent which holds detailed information about this event.
	 */
    @Override
    public void publicMessage(MessageEvent e) {}

    
    
	/**
	 * Empty implementation of {@link MessageListener#privateMessage(MessageEvent)}.
	 * @param e The MessageEvent which holds detailed information about this event.
	 */
    @Override
    public void privateMessage(MessageEvent e) {}
    
    
    
	/**
	 * Empty implementation of {@link MessageListener#actionMessage(MessageEvent)}.
	 * @param e The MessageEvent which holds detailed information about this event.
	 */
    @Override
    public void actionMessage(MessageEvent e) {}
}
