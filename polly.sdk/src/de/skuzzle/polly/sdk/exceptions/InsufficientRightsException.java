package de.skuzzle.polly.sdk.exceptions;

import de.skuzzle.polly.sdk.model.User;
import de.skuzzle.polly.sdk.roles.SecurityObject;

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
	private SecurityObject object;
	
	public InsufficientRightsException() {
		super();
	}
	
	
	public InsufficientRightsException(SecurityObject object) {
	    super("");
	    this.object = object;
	}
	
	
	/**
	 * Gets the security object that could not be accessed.
	 * 
	 * @return The SecurityObject.
	 */
    public SecurityObject getObject() {
        return this.object;
    }
}
