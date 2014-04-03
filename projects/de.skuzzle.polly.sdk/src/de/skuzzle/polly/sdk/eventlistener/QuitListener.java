package de.skuzzle.polly.sdk.eventlistener;

/**
 * This listener listens for quit events of other users.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public interface QuitListener extends IrcEventListener {
    
	/**
	 * This method is called whenever a user quits from the network. Detailed information
	 * about this event are provided via the {@link QuitEvent}.
	 * @param e Detailed information about this quit.
	 */
    public void quited(QuitEvent e);
}
