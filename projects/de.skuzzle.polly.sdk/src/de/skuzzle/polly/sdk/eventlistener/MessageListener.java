package de.skuzzle.polly.sdk.eventlistener;

import de.skuzzle.polly.tools.events.Dispatch;

/**
 * This listener listens for message events on any channel.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public interface MessageListener extends IrcEventListener {
    
    public final static Dispatch<MessageListener, MessageEvent> PUBLIC_MESSAGE = 
            new Dispatch<MessageListener, MessageEvent>() {
        @Override
        public void dispatch(MessageListener listener, MessageEvent event) {
            listener.publicMessage(event);
        }
    };
    
    public final static Dispatch<MessageListener, MessageEvent> PRIVATE_MESSAGE = 
            new Dispatch<MessageListener, MessageEvent>() {
        @Override
        public void dispatch(MessageListener listener, MessageEvent event) {
            listener.privateMessage(event);
        }
    };
    
    public final static Dispatch<MessageListener, MessageEvent> ACTION_MESSAGE = 
            new Dispatch<MessageListener, MessageEvent>() {
        @Override
        public void dispatch(MessageListener listener, MessageEvent event) {
            listener.actionMessage(event);
        }
    };
    
    public final static Dispatch<MessageListener, MessageEvent> NOTICE_MESSAGE = 
            new Dispatch<MessageListener, MessageEvent>() {
        @Override
        public void dispatch(MessageListener listener, MessageEvent event) {
            listener.noticeMessage(event);
        }
    };
    
    
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
