package de.skuzzle.polly.parsing.ast.visitor.resolving;


import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.Typespace;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.DepthFirstVisitor;


public abstract class AbstractTypeResolver extends DepthFirstVisitor {
    
    protected Namespace nspace;
    protected final Namespace rootNs;
    protected final Typespace types;
    
    
    
    public AbstractTypeResolver(Namespace namespace) {
        // create temporary namespace for executing user
        this.rootNs = namespace.enter(false);
        this.nspace = this.rootNs;
        this.types = new Typespace();
    }
    
    
    
    /**
     * Gets the currently used namespace.
     * 
     * @return Current namespace.
     */
    public Namespace getCurrentNameSpace() {
        return this.nspace;
    }
    
    
    
    /**
     * Gets the root namespace.
     * 
     * @return The root namespace.
     */
    public Namespace getRootNamespace() {
        return this.rootNs;
    }
    
    
    
    /**
     * Creates a new sub namespace of the current namespace and sets that new namespace
     * as the current one.
     * 
     * @return The created namespace.
     */
    protected Namespace enter() {
        return this.nspace = this.nspace.enter(true);
    }
    
    
    
    /**
     * Sets the current namespace as the parent of the current namespace.
     * 
     * @return The parent of the former current namespace.
     */
    protected Namespace leave() {
        return this.nspace = this.nspace.getParent();
    }
    
    
    
    /**
     * Reports an error at the given node's position.
     * 
     * @param node Node which position will be used for the {@link ASTTraversalException}.
     * @param error Error message.
     * @throws ASTTraversalException Will always be thrown.
     */
    protected void reportError(Node node, String error) 
            throws ASTTraversalException {
        throw new ASTTraversalException(node.getPosition(), error);
    }
    
    
    
    protected void typeError(Expression exp) throws ASTTraversalException {
        throw new ASTTraversalException(exp.getPosition(), "Type error @ " + exp);
    }
}
