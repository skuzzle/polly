package de.skuzzle.polly.sdk.exceptions;

import de.skuzzle.polly.sdk.CommandManager;
import de.skuzzle.polly.sdk.Signature;

/**
 * This exception is thrown by the {@link CommandManager} upon resolving a command by its
 * signature. If the command exists but has not the given signature, this exception is 
 * thrown holding the signature that was tried to resolve.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public class UnknownSignatureException extends Exception {

	private static final long serialVersionUID = 1L;

	private Signature signature;
	
	
	/**
	 * Creates a new {@link UnknownSignatureException} for the given signature.
	 * @param signature The signature that could not be resolved.
	 */
	public UnknownSignatureException(Signature signature) {
		this.signature = signature;
	}
	
	
	
	/**
	 * Returns the signature that could not be resolved.
	 * @return the signature.
	 */
	public Signature getSignature() {
		return this.signature;
	}
}