package de.skuzzle.polly.sdk.exceptions;

import de.skuzzle.polly.sdk.model.User;

/**
 * This exception is thrown upon executing a command if the executing users userlevel
 * is too low.
 * 
 * @see User#getUserLevel()
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public class InsufficientRightsException extends Exception {

	private static final long serialVersionUID = 1L;

	
	public InsufficientRightsException() {
		super();
	}

	
	public InsufficientRightsException(String message) {
		super(message);
	}
	
	

}
