package de.skuzzle.polly.sdk.eventlistener;

import de.skuzzle.polly.tools.events.Dispatch;


/**
 * This listener listens for channel-mode changes.
 * 
 * @author Simon
 * @version 1.0
 * @since Beta 0.5
 */
public interface ChannelModeListener extends IrcEventListener {

    public final static Dispatch<ChannelModeListener, ChannelModeEvent> MODE_CHANGED = 
            new Dispatch<ChannelModeListener, ChannelModeEvent>() {
        @Override
        public void dispatch(ChannelModeListener listener, ChannelModeEvent event) {
            listener.channelModeChanged(event);
        }
    };
    
    
    
    /**
     * This method is called whenever the mode of a certain channel is changed. Detailed
     * information about the changed modes are contained within the 
     * {@link ChannelModeEvent}.
     * @param e The ChannelModeEvent which holds detailed information about this event.
     */
    public abstract void channelModeChanged(ChannelModeEvent e);
}