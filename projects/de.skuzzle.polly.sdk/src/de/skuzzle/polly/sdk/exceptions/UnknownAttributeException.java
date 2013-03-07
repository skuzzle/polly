package de.skuzzle.polly.sdk.exceptions;

/**
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public class UnknownAttributeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnknownAttributeException() {
        super();
    }

    public UnknownAttributeException(String message) {
        super(message);
    }
}