package de.skuzzle.polly.core.parser.ast.visitor.resolving;


import java.util.Collection;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.Node;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.Call;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.DepthFirstVisitor;
import de.skuzzle.polly.core.parser.problems.ProblemReporter;


public abstract class AbstractTypeResolver extends DepthFirstVisitor {
    
    protected Namespace nspace;
    protected final Namespace rootNs;
    protected final ProblemReporter reporter;
    
    
    public AbstractTypeResolver(Namespace namespace, ProblemReporter reporter) {
        // create temporary namespace for executing user
        this.rootNs = namespace.enter();
        this.nspace = this.rootNs;
        this.reporter = reporter;
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
        return this.nspace = this.nspace.enter();
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
        this.reporter.semanticProblem(error, position);
    }
    
    
    
    protected void ambiguousCall(Call call, Collection<Type> types) 
            throws ASTTraversalException {
        this.reportError(call.getRhs(), 
            "AmbiguousCall call. Matching types: " + types);
    }
    
    
    
    protected void typeError(Expression exp, Type expected, Type found) 
            throws ASTTraversalException {
        
        this.reporter.typeProblem(expected, found, exp.getPosition());
    }
}
