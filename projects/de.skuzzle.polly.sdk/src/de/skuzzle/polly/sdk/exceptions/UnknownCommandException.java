package de.skuzzle.polly.sdk.exceptions;

/**
 * This exception is thrown by the {@link CommandManager} if it tries to access a command
 * that does not exist.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public class UnknownCommandException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnknownCommandException() {
		super();
	}

	
	
	public UnknownCommandException(String message) {
		super(message);
	}
}
