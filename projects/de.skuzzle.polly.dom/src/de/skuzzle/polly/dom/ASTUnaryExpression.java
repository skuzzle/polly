package de.skuzzle.polly.dom;

import de.skuzzle.parser.dom.ASTNodeProperty;


public interface ASTUnaryExpression extends ASTExpression, ASTCallable {

    /** Property for operands of unary expressions */
    public final static ASTNodeProperty OPERAND = new ASTNodeProperty("OPERAND");
    
    /** Property for operators of unary expressions */
    public final static ASTNodeProperty OPERATOR = new ASTNodeProperty("OPERATOR");
    

    
    /**
     * Gets the operand of this expression.
     * @return The operand.
     */
    public ASTExpression getOperand();
    
    /**
     * Sets the operand of this expression.
     * @param expression The new operand.
     */
    public void setOperand(ASTExpression expression);
    
    /**
     * Gets the operator of this expression.
     * @return The operator.
     */
    public ASTOperator getOperator();
    
    /**
     * Sets the operator of this expression.
     * @param op The new operator.
     */
    public void setOperator(ASTOperator op);
    
    @Override
    public ASTUnaryExpression getOrigin();
    
    @Override
    public ASTUnaryExpression deepOrigin();
    
    @Override
    public ASTUnaryExpression copy();
}
