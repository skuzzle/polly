package de.skuzzle.polly.sdk;

import java.util.List;

/**
 * <p>This class extends a normal signature to provide an additional help text.</p>
 * 
 * <p>Formal signatures are used to define the parameters for your commands. Therefore
 * its helpful to provide a suitable help message for the signature which describes
 * what your command does with the given parameters.</p>
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public class FormalSignature extends Signature {
	
	private String help;

	
	/**
	 * Creates a new FormalSignature
	 * @param name The name of the command which this signature is for.
	 * @param id The formal id of this signature.
	 * @param help The help String for this signature.
	 * @param parameters The formal parameters of this signature.
	 */
	public FormalSignature(String name, int id, String help, List<Types> parameters) {
		super(name, id, parameters);
		this.help = help;
	}
	
	
	
	/**
	 * Creates a new FormalSignature
	 * @param name The name of the command which this signature is for.
	 * @param id The formal id of this signature.
	 * @param help The help String for this signature.
	 * @param parameters The formal parameters of this signature.
	 */
	public FormalSignature(String name, int id, String help, Types... parameters) {
		super(name, id, parameters);
		this.help = help;
	}
	
	
	
	/**
	 * Gets the help text.
	 * @return The help text.
	 */
	public String getHelp() {
		return this.help;
	}
}
