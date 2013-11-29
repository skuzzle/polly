package de.skuzzle.polly.sdk.eventlistener;

import de.skuzzle.polly.tools.events.Dispatch;

/**
 * This listener receives a message whenever polly sends messages to the irc.
 * 
 * @author Simon
 * @since 0.7
 */
public interface MessageSendListener extends IrcEventListener {
    
    public final static Dispatch<MessageSendListener, OwnMessageEvent> MESSAGE_SENT = 
            new Dispatch<MessageSendListener, OwnMessageEvent>() {
        @Override
        public void dispatch(MessageSendListener listener, OwnMessageEvent event) {
            listener.messageSent(event);
        }
    };
    
    /**
     * This method gets called whenever polly sends a message to the irc.
     * 
     * @param e The message that has been sent.
     */
    public abstract void messageSent(OwnMessageEvent e);
}
