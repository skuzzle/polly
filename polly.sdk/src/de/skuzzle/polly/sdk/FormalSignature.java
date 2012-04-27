package de.skuzzle.polly.sdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.roles.SecurityObject;

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
public class FormalSignature extends Signature implements SecurityObject {
    
	private String help;
	private List<Parameter> formalParameters;
	private String permissionName;

	
	
	/**
     * Creates a new FormalSignature with no permission.
     * 
     * @param name The name of the command which this signature is for.
     * @param id The formal id of this signature.
     * @param help The help String for this signature.
     * @param parameters The formal parameters of this signature.
     */
	public FormalSignature(String name, int id, String help, Parameter... parameters) {
	    this(name, id, help, RoleManager.NONE_PERMISSIONS, parameters);
	}
	
	
	
    /**
     * Creates a new FormalSignature which can only be executed by a user which has the
     * given permission.
     * 
     * @param name The name of the command which this signature is for.
     * @param id The formal id of this signature.
     * @param help The help String for this signature.
     * @param permissionName The required permission to execute this signature.
     * @param parameters The formal parameters of this signature.
     */
    public FormalSignature(String name, int id, String help, String permissionName, 
            Parameter... parameters) {
        super(name, id, paramToType(parameters));
        this.formalParameters = Arrays.asList(parameters);
        this.help = help;
        this.permissionName = permissionName;
    }
    
	
	
	private static List<Types> paramToType(Parameter[] parameters) {
	    List<Types> result = new ArrayList<Types>(parameters.length);
	    for (Parameter param : parameters) {
	        result.add(param.getType());
	    }
	    return result;
	}
	
	
	
	@Override
	public String getRequiredPermission() {
	    return this.permissionName;
	}
	
	
	
	/**
	 * Gets the help text.
	 * @return The help text.
	 */
	public String getHelp() {
		String help = this.help.endsWith(".") ? this.help + " " : this.help + ". ";
		help += "Signatur: ";
		Iterator<Parameter> it = this.formalParameters.iterator();
		while (it.hasNext()) {
		    Parameter param = it.next();
		    help += param.toString();
		    if (it.hasNext()) {
		        help += ", ";
		    }
		    
		}
		return help;
	}
	
	
	
	public String getSample() {
	    StringBuilder result = new StringBuilder();
        Iterator<Parameter> it = this.formalParameters.iterator();
        while (it.hasNext()) {
            Parameter param = it.next();
            result.append(param.getType().getSample());
            if (it.hasNext()) {
                result.append(" ");
            }
        }
        return result.toString();
	}
}
