package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;


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
    public <T extends Node> void replaceChild(T current, T newChild) {
        if (current == this.lhs) {
            this.lhs = (Expression) newChild;
        } else if (current == this.rhs) {
            this.rhs = (Expression) newChild;
        } else {
            super.replaceChild(current, newChild);
        }
    }
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitAccess(this);
    }
}
