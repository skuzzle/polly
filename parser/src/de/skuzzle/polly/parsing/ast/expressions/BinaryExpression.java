package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.Visitor;

/**
 * Represents a binary expression Node in the AST. Binary expressions consist of a 
 * left expression, an operator and a right expression.
 * 
 * @author Simon Taddiken
 *
 */
public class BinaryExpression extends Expression {

    private Expression left;
    private BinaryOperator operator;
    private Expression right;
    
    
    
    /**
     * Creates a new BinaryExpression.
     * 
     * @param position The position of this binary expression.
     * @param left Left expression.
     * @param operator Operator.
     * @param right Right expression.
     */
    public BinaryExpression(Position position, Expression left, BinaryOperator operator, 
            Expression right) {
        super(position);
        this.left = left;
        this.operator = operator;
        this.right = right;
    }


    
    /**
     * Gets the left expression of this binary expression.
     * 
     * @return The left expression.
     */
    public Expression getLeft() {
        return this.left;
    }
    
    
    
    /**
     * Gets the operator of this binary expression.
     * 
     * @return The operator.
     */
    public BinaryOperator getOperator() {
        return this.operator;
    }
    
    
    
    /**
     * Gets the right expression of this binary expression.
     * 
     * @return The right expression.
     */
    public Expression getRight() {
        return this.right;
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitBinaryExp(this);
    }
}
