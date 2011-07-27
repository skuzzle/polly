package de.skuzzle.polly.sdk.exceptions;

import de.skuzzle.polly.sdk.Disposable;



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

    /**
     * {@inheritDoc}
     */
    public DisposingException() {
        super();
    }

    
    
    /**
     * {@inheritDoc}
     * @param message {@inheritDoc}
     * @param cause  {@inheritDoc}
     */
    public DisposingException(String message, Throwable cause) {
        super(message, cause);
    }

    
    
    /**
     * {@inheritDoc}
     * @param message {@inheritDoc}
     */
    public DisposingException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     * @param cause  {@inheritDoc}
     */
    public DisposingException(Throwable cause) {
        super(cause);
    }
}
