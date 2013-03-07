package de.skuzzle.polly.sdk.exceptions;


/**
 * This exception is thrown when trying to add a user with an invalid name.
 * User names must match 
 * @author Simon
 *
 */
public class InvalidUserNameException extends Exception {

    private static final long serialVersionUID = 1L;

    
    public InvalidUserNameException(String name) {
        super(name);
    }
    
    
}
