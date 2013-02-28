package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ProductLiteral;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversal;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Transformation;
import de.skuzzle.polly.parsing.ast.visitor.ASTVisitor;

/**
 * This class represents a call expression. The left hand side of a call must evaluate
 * to a {@link FunctionLiteral}. 
 * 
 * @author Simon Taddiken
 */
public class Call extends Expression {
    
    private static final long serialVersionUID = 1L;
    
    private Expression lhs;
    private final ProductLiteral rhs;
    
    
    
    /**
     * Creates a new Call.
     * 
     * @param position Position within the source.
     * @param lhs Left hand side of the call. The expression being 'called'.
     * @param rhs Right hand side if the call. This represents the parameters handed to
     *          the called expression.
     */
    public Call(Position position, Expression lhs, ProductLiteral rhs) {
        super(position);
        this.rhs = rhs;
        this.lhs = lhs;
    }

    
    
    /**
     * Gets the left hand side of this call (the expression being 'called').
     * 
     * @return The called expression.
     */
    public Expression getLhs() {
        return this.lhs;
    }

    
    
    /**
     * Gets the right hand side of this call (the actual parameters of this call).
     * 
     * @return The actual parameters.
     */
    public ProductLiteral getRhs() {
        return this.rhs;
    }
    
    
    
    @Override
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return visitor.visit(this);
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
    
    
    
    @Override
    public Expression transform(Transformation transformation) throws ASTTraversalException {
        return transformation.transformCall(this);
    }
}
