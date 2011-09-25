package de.skuzzle.polly.sdk.eventlistener;

import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.model.User;

/**
 * This is the base event class for events raised by the {@link UserManager}.
 * 
 * @author Simon
 * @since Beta 0.6
 */
public class UserEvent  {

    private UserManager source;
    
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
     * @param autologgoff Whether this event was an autologoff.
     */
    public UserEvent(UserManager source, User user, boolean autologoff) {
        this.source = source;
        this.user = user;
        this.autologoff = autologoff;
    }
    
    
    /**
     * Gets the source of this event.
     * @return The source {@link UserManager}.
     */
    public UserManager getSource() {
        return this.source;
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