package de.skuzzle.polly.core.parser.ast.expressions;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversal;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ASTVisitor;
import de.skuzzle.polly.core.parser.ast.visitor.Transformation;


/**
 * Inspect statement that allows to read information of declarations from a 
 * {@link Namespace}. As there are no real statements, this expression will return a
 * string containing those information upon execution.
 * 
 * @author Simon Taddiken
 */
public class Inspect extends Expression {
    
    private final Expression access;
    private final boolean global;
    
    
    /**
     * Creates a new Inspect node.
     * 
     * @param position Source position of this expression.
     * @param access Expression which either accesses a declaration directly or through
     *          a preceded namespace.
     * @param global Whether declaration information should be retrieved from global
     *          namespace.
     */
    public Inspect(Position position, Expression access, boolean global) {
        super(position);
        this.access = access;
        this.global = global;
    }

    
    
    /**
     * Gets whether declaration information should be retrieved from global
     * namespace.
     * 
     * @return Whether declaration information should be retrieved from global
     *          namespace.
     */
    public boolean isGlobal() {
        return this.global;
    }
    


    /**
     * Gets the expression that references the declaration to retrieve information for.
     * 
     * @return The expression to access the declaration.
     */
    public Expression getAccess() {
        return this.access;
    }
    
    

    @Override
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return visitor.visit(this);
    }
    
    
    
    @Override
    public Expression transform(Transformation transformation) 
            throws ASTTraversalException {
        return transformation.transformInspect(this);
    }
    
    
    
    @Override
    public boolean traverse(ASTTraversal visitor) throws ASTTraversalException {
        switch (visitor.before(this)) {
        case ASTTraversal.SKIP: return true;
        case ASTTraversal.ABORT: return false;
        }
        this.access.traverse(visitor);
        return visitor.after(this) == ASTTraversal.CONTINUE;
    }
}
