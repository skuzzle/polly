package de.skuzzle.polly.sdk.exceptions;

import de.skuzzle.polly.sdk.roles.SecurityObject;

/**
 * This exception is thrown upon executing a command if the executing user has 
 * insufficient permissions to run it. Additionally its thrown whenever the 
 * {@link RoleManager} needs to deny access to a {@link SecurityObject}.
 * 
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
	    super(""); //$NON-NLS-1$
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
