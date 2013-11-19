package de.skuzzle.polly.sdk.eventlistener;

import de.skuzzle.polly.sdk.IrcManager;

/**
 * This class represents an irc quit event. Its raised if a user quits from the network.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public class QuitEvent extends IrcEvent {

    private String quitMessage;
    private IrcUser user;
    
    
    
    /**
     * Creates a new QuitEvent.
     * @param source The source {@link IrcManager}.
     * @param user The user who quit.
     * @param message The quit message.
     */
    public QuitEvent(IrcManager source, IrcUser user, String message) {
        super(source);
        this.user = user;
        this.quitMessage = message;
    }
    
    
    
    /**
     * Gets the user who quit.
     * @return The user.
     */
    public IrcUser getUser() {
        return this.user;
    }
    
    
    
    /**
     * Gets the quit message.
     * @return The quit message.
     */
    public String getQuitMessage() {
        return this.quitMessage;
    }
    
    
    
    /**
     * Formats this event to a suitable String.
     * @return A String representation of this event.
     */
    @Override
    public String toString() {
    	String message = this.quitMessage.length() == 0 
    		? "" : "(" + this.quitMessage + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    	return "*** QUIT " + this.user + message; //$NON-NLS-1$
    }
}
