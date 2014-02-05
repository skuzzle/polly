package de.skuzzle.polly.dom;

import de.skuzzle.parser.dom.ASTNodeProperty;



public interface ASTCallExpression extends ASTExpression {

    /** Property for lhs node */
    public final static ASTNodeProperty LHS_EXPRESSION = 
            new ASTNodeProperty("LHS_EXPRESSION");
    
    /** Property for rhs node */
    public final static ASTNodeProperty RHS_EXPRESSION = 
            new ASTNodeProperty("RHS_EXPRESSION");
    
    
    /**
     * Gets the expression representing the left hand side of this call, thus this 
     * expression identifies the function to call.
     * 
     * @return The left hand side of this call.
     */
    public ASTExpression getLhs();
    
    /**
     * Sets the expression representing the left hand side of this call.
     * 
     * @param lhs The new left hand side. Must not be <code>null</code>.
     */
    public void setLhs(ASTExpression lhs);
    
    /**
     * Gets the actual parameters of this call
     * 
     * @return The parameters
     */
    public ASTProductExpression getRhs();
    
    /**
     * Sets the actual parameters of this call
     * @param rhs The new parameters. Must not be <code>null</code>.
     */
    public void setRhs(ASTProductExpression rhs);
    
    @Override
    public ASTCallExpression copy();
}
