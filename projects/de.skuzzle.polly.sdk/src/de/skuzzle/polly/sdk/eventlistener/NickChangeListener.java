package de.skuzzle.polly.sdk.eventlistener;


/**
 * This listener listens for nickchange events.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public interface NickChangeListener extends IrcEventListener {
    
	/**
	 * This method is called whenever a user changes his nickname. Detailed information
	 * are provided via the {@link NickChangeEvent}.
	 * @param e Detailed information about this nick change.
	 */
    public void nickChanged(NickChangeEvent e);
}
