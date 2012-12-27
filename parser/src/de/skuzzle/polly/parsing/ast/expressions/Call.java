package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.expressions.literals.ProductLiteral;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;


public class Call extends Expression {
    
    private static final long serialVersionUID = 1L;
    
    private Expression lhs;
    private final ProductLiteral rhs;
    
    
    
    public Call(Position position, Expression lhs, ProductLiteral rhs) {
        super(position);
        this.rhs = rhs;
        this.lhs = lhs;
    }

    
    
    public Expression getLhs() {
        return this.lhs;
    }

    
    
    public ProductLiteral getRhs() {
        return this.rhs;
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitCall(this);
    }
}
