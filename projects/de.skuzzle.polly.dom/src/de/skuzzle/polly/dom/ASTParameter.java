package de.skuzzle.polly.dom;

import de.skuzzle.polly.dom.types.Type;


public interface ASTParameter extends ASTPollyNode {

    /**
     * Gets the parameter's name.
     * 
     * @return The name.
     */
    public ASTName getName();
    
    /**
     * Sets the parameter's name.
     * 
     * @param name The new name.
     */
    public void setName(ASTName name);
    
    /**
     * Gets the parameter's type.
     * 
     * @return The type.
     */
    public Type getType();
    
    /**
     * Sets the parameter's type.
     * 
     * @param type The new type.
     */
    public void setType(Type type);
    
    @Override
    public ASTParameter getOrigin();
    
    @Override
    public ASTParameter deepOrigin();
    
    @Override
    public ASTParameter copy();
}