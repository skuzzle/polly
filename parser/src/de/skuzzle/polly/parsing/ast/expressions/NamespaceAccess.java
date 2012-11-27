package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;


public class NamespaceAccess extends Expression {

    private static final long serialVersionUID = 1L;
    
    private final Expression lhs;
    private final Expression rhs;
    
    
    
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
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitAccess(this);
    }
}
