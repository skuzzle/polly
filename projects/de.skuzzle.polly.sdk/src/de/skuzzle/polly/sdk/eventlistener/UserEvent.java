package de.skuzzle.polly.sdk.eventlistener;

import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.tools.events.Event;

/**
 * This is the base event class for events raised by the {@link UserManager}.
 * 
 * @author Simon
 * @since Beta 0.6
 */
public class UserEvent extends Event<UserManager> {

    private User user;
    
    private boolean autologoff;
    
    /**
     * Creates a new UserEvent (with autologoff = <code>false</code>).
     * 
     * @param source The source of this event.
     * @param user The user object.
     */
    public UserEvent(UserManager source, User user) {
        this(source, user, false);
    }
    
    
    /**
     * Creates a new UserEvent.
     * 
     * @param source The source of this event.
     * @param user The user object.
     * @param autologoff Whether this event was an autologoff.
     */
    public UserEvent(UserManager source, User user, boolean autologoff) {
        super(source);
        this.user = user;
        this.autologoff = autologoff;
    }
    
    
    
    /**
     * Gets the user.
     * @return The user.
     */
    public User getUser() {
        return this.user;
    }
    
    
    /**
     * If this event was fired because an user was automatically logged off, this 
     * mathod returns <code>true</code>. In all other cases it will return 
     * <code>false</code>.
     * 
     * @return Whether this was an autologoff.
     */
    public boolean isAutoLogoff() {
        return this.autologoff;
    }
}