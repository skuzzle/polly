package de.skuzzle.polly.dom;

import de.skuzzle.parser.dom.ASTNodeProperty;




public interface ASTName extends ASTPollyNode {
    
    /** Property if this name is part of a qualified name */
    public final static ASTNodeProperty PART_OF_QUALIFICATION = new ASTNodeProperty(
            "PART_OF_QUALIFICATION");

    
    
    /**
     * Gets the name as String which this node represents.
     * 
     * @return The name
     */
    public String getName();
    
    @Override
    public ASTName getOrigin();
    
    @Override
    public ASTName deepOrigin();
    
    @Override
    public ASTName copy();
}