package de.skuzzle.polly.core.parser.ast.expressions;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversal;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ASTVisitor;
import de.skuzzle.polly.core.parser.ast.visitor.Transformation;


/**
 * This expression allows to access declarations in different namespaces than the current
 * execution context. 
 * 
 * @author Simon Taddiken
 */
public class NamespaceAccess extends Expression {
    
    private Expression lhs;
    private Expression rhs;
    
    
    
    /**
     * Creates a new NamespaceAccess.
     * 
     * @param position Position of this expression within the source.
     * @param lhs Left handed side of this access. Must be of type {@link VarAccess} 
     *          (which represents the name of the {@link Namespace} to access.
     * @param rhs Right handed side of this access. Must be of type {@link VarAccess}
     *          and represents the name of the variable in the namespace being accessed. 
     */
    public NamespaceAccess(Position position, Expression lhs, Expression rhs) {
        super(position);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    
    
    /**
     * Gets the left handed side of this expression (the namespace being accessed).
     * 
     * @return The expression which represents the namespace being accessed.
     */
    public Expression getLhs() {
        return this.lhs;
    }
    
    
    
    /**
     * Sets the left hand side of this expression (the namespace being accessed).
     * 
     * @param lhs The expression which represents the namespace being accessed.
     */
    public void setLhs(Expression lhs) {
        this.lhs = lhs;
    }
    
    
    
    /**
     * Gets the right handed side of this expression (variable in the namespace being 
     * accessed).
     * 
     * @return The expression which represents the variable being accessed.
     */
    public Expression getRhs() {
        return this.rhs;
    }
    
    
    
    /**
     * Sets the right hand side of this expression (variable that is being accessed in the
     * context of the namespace represented by the LHS of this expression.
     * 
     * @param rhs The expression which represents the variable being accessed.
     */
    public void setRhs(Expression rhs) {
        this.rhs = rhs;
    }

    
    
    @Override
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return visitor.visit(this);
    }
    
    
    
    @Override
    public Expression transform(Transformation transformation) 
            throws ASTTraversalException {
        return transformation.transformAccess(this);
    }
    
    
    
    @Override
    public boolean traverse(ASTTraversal visitor) throws ASTTraversalException {
        switch (visitor.before(this)) {
        case ASTTraversal.SKIP: return true;
        case ASTTraversal.ABORT: return false;
        }
        if (!this.lhs.traverse(visitor)) {
            return false;
        }
        if (!this.rhs.traverse(visitor)) {
            return false;
        }
        return visitor.after(this) == ASTTraversal.CONTINUE;
    }
}
