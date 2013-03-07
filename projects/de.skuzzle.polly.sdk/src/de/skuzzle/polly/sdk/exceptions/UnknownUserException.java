package de.skuzzle.polly.sdk.exceptions;

/**
 * The exception is thrown if you try to retrieve a user which does not exist.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public class UnknownUserException  extends Exception {

    private static final long serialVersionUID = 1L;

    public UnknownUserException() {
        super();
    }

    public UnknownUserException(String message) {
        super(message);
    }
}
