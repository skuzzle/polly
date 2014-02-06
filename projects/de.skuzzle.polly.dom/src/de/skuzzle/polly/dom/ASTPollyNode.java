package de.skuzzle.polly.dom;

import de.skuzzle.parser.dom.ASTNode;

/**
 * Superclass of all other polly AST nodes.
 * 
 * @author Simon Taddiken
 */
public interface ASTPollyNode extends ASTNode<ASTVisitor> {
    
    /**
     * Gets an {@link ASTNodeFactory} for creating new nodes.
     * 
     * @return The node factory.
     */
    public ASTNodeFactory getNodeFactory();
    
    @Override
    public ASTPollyNode deepOrigin();
    
    @Override
    public ASTPollyNode getOrigin();
    
    @Override
    public ASTPollyNode copy();
}
