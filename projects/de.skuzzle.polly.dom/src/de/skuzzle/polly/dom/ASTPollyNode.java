package de.skuzzle.polly.dom;

import de.skuzzle.parser.dom.ASTNode;

/**
 * Superclass of all other polly AST nodes.
 * 
 * @author Simon Taddiken
 */
public interface ASTPollyNode extends ASTNode<ASTVisitor> {
    
    @Override
    public ASTPollyNode copy();
}
