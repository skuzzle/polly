package de.skuzzle.polly.sdk.exceptions;

/**
 * This exception indicates a database error. Mostly this exception is thrown if 
 * committing a transaction fails.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public class DatabaseException extends Exception {

    private static final long serialVersionUID = 1L;

    public DatabaseException() {
        super();
    }

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(Throwable cause) {
        super(cause);
    }
}
