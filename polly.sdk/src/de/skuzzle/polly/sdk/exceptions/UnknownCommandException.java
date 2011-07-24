package de.skuzzle.polly.sdk.exceptions;

import de.skuzzle.polly.sdk.CommandManager;

/**
 * This exception is thrown by the {@link CommandManager} if it tries to access a command
 * that does not exist.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public class UnknownCommandException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnknownCommandException() {
		super();
	}

	
	
	public UnknownCommandException(String message) {
		super(message);
	}
}
