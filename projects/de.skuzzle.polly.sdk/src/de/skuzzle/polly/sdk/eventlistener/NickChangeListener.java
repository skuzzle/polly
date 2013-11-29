package de.skuzzle.polly.sdk.eventlistener;

import de.skuzzle.polly.tools.events.Dispatch;

/**
 * This listener listens for nickchange events.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public interface NickChangeListener extends IrcEventListener {
    
    public final static Dispatch<NickChangeListener, NickChangeEvent> NICK_CHANGED = 
            new Dispatch<NickChangeListener, NickChangeEvent>() {
        @Override
        public void dispatch(NickChangeListener listener, NickChangeEvent event) {
            listener.nickChanged(event);
        }
    };
    
	/**
	 * This method is called whenever a user changes his nickname. Detailed information
	 * are provided via the {@link NickChangeEvent}.
	 * @param e Detailed information about this nick change.
	 */
    public void nickChanged(NickChangeEvent e);
}
