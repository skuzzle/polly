package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversal;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Transformation;
import de.skuzzle.polly.parsing.ast.visitor.ASTVisitor;


/**
 * Represents the access of a variable.
 * 
 * @author Simon Taddiken
 */
public class VarAccess extends Expression {
    
    private ResolvableIdentifier identifier;
    
    
    /**
     * Creates a new VarAccess expression.
     * 
     * @param position The position of this expression within the source.
     * @param identifier The name of the variable to access.
     */
    public VarAccess(Position position, ResolvableIdentifier identifier) {
        super(position);
        if (identifier == null) {
            throw new NullPointerException("identifier is null");
        }
        this.identifier = identifier;
    }
    
    
    
    /**
     * Gets the name of the variable that is accessed.
     * 
     * @return The variables name.
     */
    public ResolvableIdentifier getIdentifier() {
        return this.identifier;
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
        if (!this.identifier.traverse(visitor)) {
            return false;
        }
        return visitor.after(this) == ASTTraversal.CONTINUE;
    }
    
    
    
    @Override
    public Expression transform(Transformation transformation) 
            throws ASTTraversalException {
        return transformation.transformVarAccess(this);
    }
    
    
    @Override
    public String toString() {
        return "[VarAccess: " + this.identifier + ", " + super.toString() + "]";
    }
}
