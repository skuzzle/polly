package de.skuzzle.polly.sdk.exceptions;

/**
 * 
 * @author Simon
 * @since Beta 0.2
 */
public class AlreadySignedOnException extends Exception {

    private static final long serialVersionUID = 1L;

    public AlreadySignedOnException() {
        super();
    }

    public AlreadySignedOnException(String message) {
        super(message);
    }

    
}
