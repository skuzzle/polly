package de.skuzzle.polly.sdk.eventlistener;

/**
 * This listener receives a message whenever polly sends messages to the irc.
 * 
 * @author Simon
 * @since 0.7
 */
public interface MessageSendListener extends IrcEventListener {
    
    /**
     * This method gets called whenever polly sends a message to the irc.
     * 
     * @param e The message that has been sent.
     */
    public abstract void messageSent(OwnMessageEvent e);
}
