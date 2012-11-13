package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;


public class NamespaceAccess extends Expression {

    private final Expression lhs;
    private final VarAccess rhs;
    
    
    
    public NamespaceAccess(Position position, Expression lhs, VarAccess rhs) {
        super(position);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    
    
    public Expression getLHS() {
        return this.lhs;
    }
    
    
    
    public VarAccess getRhs() {
        return this.rhs;
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitAccess(this);
    }
}
