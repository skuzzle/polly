package de.skuzzle.polly.sdk.exceptions;

import de.skuzzle.polly.sdk.User;

/**
 * 
 * @author Simon
 * @since Beta 0.2
 */
public class AlreadySignedOnException extends Exception {

    private static final long serialVersionUID = 1L;

    private final User user;
    
    public AlreadySignedOnException(User user) {
        super();
        this.user = user;
    }

    public AlreadySignedOnException(User user, String message) {
        super(message);
        this.user = user;
    }

    
    
    public User getUser() {
        return this.user;
    }
}
