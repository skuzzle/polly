package de.skuzzle.polly.dom;

import java.util.List;


public interface ASTListExpression extends ASTExpression {
    
    /**
     * Gets the expressions in this list. The returned list is read-only and provides
     * random access to its elements.
     * 
     * @return List of expressions in this product.
     */
    public List<? extends ASTExpression> getExpressions();
    
    /**
     * Adds another expression to this list. The literal must be of the same kind
     * 
     * @param expression The expression to add
     */
    public void addExpression(ASTExpression expression);
    
    @Override
    public ASTListExpression getOrigin();
    
    @Override
    public ASTListExpression deepOrigin();
    
    @Override
    public ASTListExpression copy();
}
