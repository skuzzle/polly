package de.skuzzle.polly.sdk.exceptions;

/**
 * This exception may be thrown upon plugin initialization to indicate that it is not
 * compatible with the current polly version.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public class IncompatiblePluginException extends Exception {

	private static final long serialVersionUID = 1L;

	public IncompatiblePluginException() {
		super();
	}

	public IncompatiblePluginException(String message) {
		super(message);
	}
}
