package de.skuzzle.polly.sdk.exceptions;

/**
 * This exception is thrown by the {@link CommandManager} upon registering a signature
 * for a command that already exists.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public class DuplicatedSignatureException extends Exception {

	private static final long serialVersionUID = 1L;

	public DuplicatedSignatureException() {
		super();
	}

	public DuplicatedSignatureException(String message) {
		super(message);
	}	
}
