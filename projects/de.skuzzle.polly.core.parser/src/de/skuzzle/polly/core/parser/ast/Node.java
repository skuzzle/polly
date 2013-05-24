package de.skuzzle.polly.core.parser.ast;

import de.skuzzle.polly.core.parser.Location;
import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversal;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ASTVisitor;
import de.skuzzle.polly.core.parser.ast.visitor.ParentSetter;
import de.skuzzle.polly.core.parser.ast.visitor.Transformation;
import de.skuzzle.polly.core.parser.ast.visitor.Visitable;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;

/**
 * Super class for all elements of the AST. It stores the parent Node and the Nodes
 * position in the input String.
 *  
 * @author Simon Taddiken
 */
public abstract class Node implements Visitable<ASTVisitor>, Equatable, Location {
    
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
     * Updates all parent edges of the subtree represented by this node. This requires
     * traversal of the whole subtree.
     * @return Reference to this node (intended for chaining methods).
     * @throws ASTTraversalException If traversal of the subtree fails for any reason.
     */
    public Node updateParents() throws ASTTraversalException {
        this.visit(new ParentSetter());
        return this;
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
    
    

    @Override
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
    
    
    
    /**
     * Visitor-style method used to transform the AST. The actual {@link Transformation}
     * implementation will decide for each node with what new node it will be replaced.
     * 
     * @param transformation The transformation to apply.
     * @return The transformed AST.
     * @throws ASTTraversalException Can be thrown during AST traversal.
     */
    public abstract Node transform(Transformation transformation) 
        throws ASTTraversalException;

    
    
    /**
     * Uses an {@link ASTTraversal} to traverse the AST in depth first order. If you want
     * to traverse the AST in different order, use the {@link #visit(ASTVisitor)} method.
     * 
     * @param visitor Visitor to traverse the AST with.
     * @return Whether traversal should continue;
     * @throws ASTTraversalException If AST traversal fails.
     */
    public abstract boolean traverse(ASTTraversal visitor) throws ASTTraversalException;
    
    
    
    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }
    
    
    
    @Override
    public Class<?> getEquivalenceClass() {
        return Node.class;
    }
    
    
    
    @Override
    public boolean actualEquals(Equatable o) {
        final Node other = (Node) o;
        return this.position.equals(other.position);
    }
}
