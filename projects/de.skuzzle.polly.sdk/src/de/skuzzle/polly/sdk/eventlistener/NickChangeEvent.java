package de.skuzzle.polly.sdk.eventlistener;

import de.skuzzle.polly.sdk.IrcManager;

/**
 * This class represents a nick change event. It holds the an {@link IrcUser} object
 * for the users old name and one for the users new name.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public class NickChangeEvent extends IrcEvent {

    private IrcUser oldUser;
    private IrcUser newUser;
    
    
    /**
     * Creates a new NickChangeEvent..
     * @param source The source {@link IrcManager}.
     * @param oldUser The {@link IrcUser} object representing the old name.
     * @param newUser The {@link IrcUser} object representing the new name.
     */
    public NickChangeEvent(IrcManager source, IrcUser oldUser, IrcUser newUser) {
        super(source);
        this.oldUser = oldUser;
        this.newUser = newUser;
    }
    
    
    
    /**
     * Gets the old named IrcUser object.
     * @return The old user object.
     */
    public IrcUser getOldUser() {
        return this.oldUser;
    }
    
    
    
    /**
     * Gets the new named IrcUser object.
     * @return The new user object.
     */
    public IrcUser getNewUser() {
        return this.newUser;
    }
    
    
    
    /**
     * Formats this event to a suitable String.
     * 
     * @return A String representation of this event.
     */
    @Override
    public String toString() {
        return "*** NICK " + this.oldUser + " -> " + this.newUser;
    }
}
