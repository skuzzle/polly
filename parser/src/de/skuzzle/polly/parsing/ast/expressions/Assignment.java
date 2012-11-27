package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;

/**
 * Assignments either declare new variables or new functions.
 * 
 * @author Simon Taddiken
 */
public class Assignment extends Expression {

    private static final long serialVersionUID = 1L;
    
    private final Expression expression;
    private final Identifier name;
    
    
    
    public Assignment(Position position, Expression expression, 
            Identifier name) {
        super(position);
        this.expression = expression;
        this.name = name;
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
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitAssignment(this);
    }
}
