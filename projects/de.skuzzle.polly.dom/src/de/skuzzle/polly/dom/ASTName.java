package de.skuzzle.polly.dom;

import de.skuzzle.polly.dom.bindings.Binding;





public interface ASTName extends ASTPollyNode {
    
    /**
     * Gets the name as String which this node represents.
     * 
     * @return The name
     */
    public String getName();
    
    /**
     * Gets the binding which belongs to this name.
     * 
     * @return The name's binding.
     */
    public Binding getBinding();
    
    @Override
    public ASTName getOrigin();
    
    @Override
    public ASTName deepOrigin();
    
    @Override
    public ASTName copy();
}