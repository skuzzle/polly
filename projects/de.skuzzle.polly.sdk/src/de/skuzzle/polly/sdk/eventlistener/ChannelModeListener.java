package de.skuzzle.polly.sdk.eventlistener;


/**
 * This listener listens for channel-mode changes.
 * 
 * @author Simon
 * @version 1.0
 * @since Beta 0.5
 */
public interface ChannelModeListener extends IrcEventListener {

    /**
     * This method is called whenever the mode of a certain channel is changed. Detailed
     * information about the changed modes are contained within the 
     * {@link ChannelModeEvent}.
     * @param e The ChannelModeEvent which holds detailed information about this event.
     */
    public abstract void channelModeChanged(ChannelModeEvent e);
}