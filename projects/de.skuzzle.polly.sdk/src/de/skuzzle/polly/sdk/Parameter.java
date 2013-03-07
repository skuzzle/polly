package de.skuzzle.polly.sdk;


/**
 * This class encapsulates a parameter name and type for formal signatures.
 * 
 * @author Simon
 * @since 0.9
 * @see FormalSignature
 */
public class Parameter {

    private String name;
    private Types type;
    
    
    /**
     * Creates a new Parameter.
     * 
     * @param name This parameters name.
     * @param type This parameters type.
     */
    public Parameter(String name, Types type) {
        this.name = name;
        this.type = type;
    }
    
    
    
    /**
     * Gets the name of this paramter.
     * 
     * @return The parameter name.
     */
    public String getName() {
        return this.name;
    }
    
    
    
    /**
     * Gets the type of this parameter.
     * 
     * @return The parameter type.
     */
    public Types getType() {
        return this.type;
    }
    
    
    
    /**
     * Gets a String representation of this parameter.
     * 
     * @return A String suitable for help messages for this parameter.
     */
    @Override
    public String toString() {
        return this.name + " (" + this.type.toString() + ")";
    };
}