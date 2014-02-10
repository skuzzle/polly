package de.skuzzle.polly.dom;

import de.skuzzle.polly.dom.bindings.Scope;

/**
 * Interface for all nodes that define a new scope.
 * 
 * @author Simon Taddiken
 */
public interface ASTScopeOwner extends ASTPollyNode {

    /**
     * Gets the scope which this node defines.
     * 
     * @return The scope of this node.
     */
    public Scope getScope();
}