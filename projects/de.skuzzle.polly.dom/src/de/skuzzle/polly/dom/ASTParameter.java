package de.skuzzle.polly.dom;

import de.skuzzle.parser.dom.ASTNodeProperty;



public interface ASTParameter extends ASTPollyNode {
    
    /** Property for the name of a parameter */
    public final static ASTNodeProperty PARAMETER_NAME = 
            new ASTNodeProperty("PARAMETER_NAME");
    
    /** Property for the type name of a parameter */
    public final static ASTNodeProperty TYPE_NAME = new ASTNodeProperty("TYPE_NAME");

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
     * Gets the parameter's type name.
     * 
     * @return The type name.
     */
    public ASTName getTypeName();
    
    /**
     * Sets the parameter's type name.
     * 
     * @param type The new type name.
     */
    public void setTypeName(ASTName type);
    
    @Override
    public ASTParameter getOrigin();
    
    @Override
    public ASTParameter deepOrigin();
    
    @Override
    public ASTParameter copy();
}