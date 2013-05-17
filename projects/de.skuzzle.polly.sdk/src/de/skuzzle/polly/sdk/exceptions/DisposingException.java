package de.skuzzle.polly.sdk.exceptions;



/**
 * Exception being thrown if disposing an object that implements {@link Disposable} 
 * fails.
 * 
 * @author Simon
 * @version 27.07.2011 5e9480b
 * @since Beta 0.5
 */
public class DisposingException extends Exception {

    private static final long serialVersionUID = 1L;

    public DisposingException() {
        super();
    }


    public DisposingException(String message, Throwable cause) {
        super(message, cause);
    }

    

    public DisposingException(String message) {
        super(message);
    }


    public DisposingException(Throwable cause) {
        super(cause);
    }
}
