package de.skuzzle.polly.dom;

import de.skuzzle.parser.dom.ASTNodeProperty;


public interface ASTBinaryExpression extends ASTExpression, ASTCallable {
    
    /** Property for left operands */
    public final static ASTNodeProperty LEFT_OPERAND = 
            new ASTNodeProperty("LEFT_OPERAND");
    
    /** Property for right operands */
    public final static ASTNodeProperty RIGHT_OPERAND = 
            new ASTNodeProperty("RIGHT_OPERAND");
    
    /** Property for operator nodes in binary expressions */
    public final static ASTNodeProperty OPERATOR = 
            new ASTNodeProperty("OPERATOR");
    
    
    /**
     * Gets an {@link ASTCallExpression} node representing this binary expression. The
     * call's left hand side will be an access to this expression's operator, the 
     * right hand side will be the product of this expression's left and right operand.
     * 
     * <p>The result will only contain unfrozen copies of the nodes involved.</p>
     * 
     * @return A call expression representing this binary expression.
     */
    public ASTCallExpression asCall();

    /**
     * Gets the left operand of this expression.
     * 
     * @return The left operand.
     */
    public ASTExpression getLeftOperand();
    
    /**
     * Sets the left operand of this expression.
     * 
     * @param exp The new left operand
     */
    public void setLeftOperand(ASTExpression exp);
    
    /**
     * Gets the right operand of this expression.
     * 
     * @return The right operand.
     */
    public ASTExpression getRightOperand();
    
    /**
     * Sets the right operand of this expression.
     * 
     * @param exp The new right operand.
     */
    public void setRightOperand(ASTExpression exp);
    
    /**
     * Gets the operator of this expression.
     * 
     * @return The operator.
     */
    public ASTOperator getOperator();
    
    /**
     * Sets the operator of this expression.
     * 
     * @param op The new operator.
     */
    public void setOperator(ASTOperator op);
    
    @Override
    public ASTBinaryExpression deepOrigin();
    
    @Override
    public ASTBinaryExpression getOrigin();
    
    @Override
    public ASTBinaryExpression copy();
}
