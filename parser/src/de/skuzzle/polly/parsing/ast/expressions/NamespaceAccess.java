package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Transformation;
import de.skuzzle.polly.parsing.ast.visitor.ASTVisitor;


public class NamespaceAccess extends Expression {

    private static final long serialVersionUID = 1L;
    
    private Expression lhs;
    private Expression rhs;
    
    
    
    public NamespaceAccess(Position position, Expression lhs, Expression rhs) {
        super(position);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    
    
    public Expression getLhs() {
        return this.lhs;
    }
    
    
    
    public Expression getRhs() {
        return this.rhs;
    }

    
    
    @Override
    public void visit(ASTVisitor visitor) throws ASTTraversalException {
        visitor.visitAccess(this);
    }
    
    
    
    @Override
    public Expression transform(Transformation transformation) 
            throws ASTTraversalException {
        return transformation.transformAccess(this);
    }
}
