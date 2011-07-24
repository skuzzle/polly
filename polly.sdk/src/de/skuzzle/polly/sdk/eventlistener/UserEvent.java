package de.skuzzle.polly.sdk.eventlistener;

import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.model.User;

/**
 * This is the base event class for events raised by the {@link UserManager}.
 * 
 * @author Simon
 * @since Beta 0.2
 */
public class UserEvent  {

    private UserManager source;
    
    private User user;
    
    /**
     * Creates a new UserEvent.
     * @param source The source of this event.
     * @param user The user object.
     */
    public UserEvent(UserManager source, User user) {
        this.source = source;
        this.user = user;
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
}