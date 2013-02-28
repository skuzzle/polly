package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversal;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Transformation;
import de.skuzzle.polly.parsing.ast.visitor.ASTVisitor;

/**
 * Assignments either declare new variables or new functions.
 * 
 * @author Simon Taddiken
 */
public class Assignment extends Expression {

    private static final long serialVersionUID = 1L;
    
    private Expression expression;
    private Identifier name;
    private final boolean isPublic;
    private final boolean isTemp;
    
    
    /**
     * Creates a new assignment.
     * 
     * @param position Source position of the assignment.
     * @param expression Expression (left handed statement) that is assigned.
     * @param name Name to which the expression is assigned.
     * @param isPublic Whether this is a public declaration.
     * @param isTemp Whether this is a temporary declaration.
     */
    public Assignment(Position position, Expression expression, 
            Identifier name, boolean isPublic, boolean isTemp) {
        super(position);
        this.expression = expression;
        this.name = name;
        this.isPublic = isPublic;
        this.isTemp = isTemp;
    }
    
    
    
    /**
     * Whether this is a public declaration.
     * 
     * @return Whether this is a public declaration.
     */
    public boolean isPublic() {
        return this.isPublic;
    }
    
    
    
    /**
     * Whether this is a temporary declaration.
     * 
     * @return Whether this is a temporary declaration.
     */
    public boolean isTemp() {
        return this.isTemp;
    }
    
    
    
    /**
     * Gets the expression that should be assigned to a variable or function.
     * 
     * @return The expression.
     */
    public Expression getExpression() {
        return this.expression;
    }
    
    
    
    /**
     * Gets the name as which the expression should be stored.
     * 
     * @return The name.
     */
    public Identifier getName() {
        return this.name;
    }
    
    

    @Override
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return visitor.visit(this);
    }
    
    
    
    @Override
    public Expression transform(Transformation transformation) 
            throws ASTTraversalException {
        return transformation.transformAssignment(this);
    }
    
    
    
    @Override
    public boolean traverse(ASTTraversal visitor) throws ASTTraversalException {
        switch (visitor.before(this)) {
        case ASTTraversal.SKIP: return true;
        case ASTTraversal.ABORT: return false;
        }
        if (!this.name.traverse(visitor)) {
            return false;
        }
        if (!this.expression.traverse(visitor)) {
            return false;
        }
        return visitor.after(this) == ASTTraversal.CONTINUE;
    }
    
    
    
    @Override
    public String toString() {
        return "[Assignment to: " + this.getName() + ", type: " + this.getUnique() + "]";
    }
}
