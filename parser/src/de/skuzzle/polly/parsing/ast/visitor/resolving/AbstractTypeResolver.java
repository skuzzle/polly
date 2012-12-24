package de.skuzzle.polly.parsing.ast.visitor.resolving;


import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.DepthFirstVisitor;


public abstract class AbstractTypeResolver extends DepthFirstVisitor {
    
    protected Namespace nspace;
    protected final Namespace rootNs;
    
    
    
    public AbstractTypeResolver(Namespace namespace) {
        // create temporary namespace for executing user
        this.rootNs = namespace.enter(false);
        this.nspace = this.rootNs;
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
        this.reportError(node.getPosition(), error);
    }
    
    
    
    protected void reportError(Position position, String error) 
            throws ASTTraversalException {
        throw new ASTTraversalException(position, error);
    }
    
    
    
    protected void ambiguosCall(Call call, Collection<Type> types) 
            throws ASTTraversalException {
        this.reportError(call.getParameterPosition(), 
            "Ambiguos call. Matching types: " + types);
    }
    
    
    
    protected void typeError(Expression exp, Type expected, Type found) 
            throws ASTTraversalException {
        throw new ASTTraversalException(exp.getPosition(), "Typefehler. Erwartet: " + 
            expected + ", gefunden: " + found);
    }
}
