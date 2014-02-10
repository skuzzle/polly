package de.skuzzle.polly.dom.bindings;

import de.skuzzle.polly.dom.types.Type;


public interface Binding {

    /**
     * Gets the name of this binding.
     * 
     * @return The name
     */
    public String getName();
    
    /**
     * Gets the type of this binding.
     * @return The binding's type.
     */
    public Type gettType();
    
    /**
     * Gets the scope to which this binding belongs.
     * 
     * @return The binding's scope.
     */
    public Scope getScope();
}
