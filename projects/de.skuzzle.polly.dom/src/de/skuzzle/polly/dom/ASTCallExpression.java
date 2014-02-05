package de.skuzzle.polly.dom;



public interface ASTCallExpression extends ASTExpression {

    /**
     * Gets the expression representing the left hand side of this call, thus this 
     * expression identifies the function to call.
     * 
     * @return The left hand side of this call.
     */
    public ASTExpression getLhs();
    
    /**
     * Gets the actual parameters of this call
     *  
     * @return The parameters
     */
    public ASTProductExpression getRhs();
    
    @Override
    public ASTCallExpression copy();
}
