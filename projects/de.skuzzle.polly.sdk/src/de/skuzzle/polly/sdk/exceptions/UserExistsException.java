package de.skuzzle.polly.sdk.exceptions;

import de.skuzzle.polly.sdk.User;

/**
 * <p>This exception is thrown if you try to add an user which already exists. It has
 * an attribute which holds the existing user.</p>
 * 
 * <p>Note that the error message string is <code>null</code></p>
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public class UserExistsException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private User user;

    /**
     * Creates a new UserExistsException.
     * 
     * @param user The existing user.
     */
    public UserExistsException(User user) {
        this.user = user;
    }
    
    
    
    /**
     * Gets the existing user.
     * 
     * @return The existing user.
     */
    public User getUser() {
        return this.user;
    }
}
