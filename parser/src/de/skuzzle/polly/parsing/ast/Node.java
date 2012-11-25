package de.skuzzle.polly.parsing.ast;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.visitor.Visitable;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;

/**
 * Super class for all elements of the AST. It stores the parent Node and the Nodes
 * position in the input String.
 *  
 * @author Simon Taddiken
 */
public abstract class Node implements Visitable<Visitor> {
    
    private Node parent;
    private Position position;

    
    
    /**
     * Creates a new Node with the given position.
     * 
     * @param position The Node's position.
     */
    public Node(Position position) {
        this.position = position;
    }
    
    
    
    /**
     * Gets the parent Node of this node. Returns <code>null</code> for the root of the
     * AST.
     * 
     * @return The parent Node.
     */
    public Node getParent() {
        return this.parent;
    }
    
    
    
    /**
     * Sets the parent Node of this Node.
     * 
     * @param parent The new parent Node. 
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }
    
    
    
    /**
     * Gets the position of this Node.
     * 
     * @return The nodes position.
     */
    public Position getPosition() {
        return this.position;
    }
    
    
    
    /**
     * Sets the position of this Node. A Node's position should always span from the
     * beginning of its left most child to the end of its right most child.
     * 
     * @param position New position for this node.
     */
    public void setPosition(Position position) {
        this.position = position;
    }
}
