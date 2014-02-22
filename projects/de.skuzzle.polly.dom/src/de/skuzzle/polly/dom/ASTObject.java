package de.skuzzle.polly.dom;

import java.util.List;


public interface ASTObject extends ASTPollyNode, ASTScopeOwner {
    
    /**
     * Gets the declared members of the object.
     * 
     * @return List of member declarations.
     */
    public List<? extends ASTVariableDeclaration> getMembers();
    
    @Override
    public ASTObject deepOrigin();
    
    @Override
    public ASTObject getOrigin();
    
    @Override
    public ASTObject copy();
}
