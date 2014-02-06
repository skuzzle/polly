package de.skuzzle.polly.dom;

import java.util.List;

import de.skuzzle.parser.dom.ASTNodeProperty;


public interface ASTProductExpression extends ASTExpression {
    
    /** Property for children of this node */
    public final static ASTNodeProperty PART_OF_PRODUCT = 
            new ASTNodeProperty("PART_OF_PRODUCT");

    /**
     * Gets the expressions in this product. The returned list is read-only and provides
     * random access to its elements.
     * 
     * @return List of expressions in this product.
     */
    public List<? extends ASTExpression> getExpressions();
    
    /**
     * Adds an expression to this product.
     * 
     * @param expression The expression to add. Must not be <code>null</code>.
     */
    public void addExpression(ASTExpression expression);
    
    @Override
    public ASTProductExpression getOrigin();
    
    @Override
    public ASTProductExpression deepOrigin();
    
    @Override
    public ASTProductExpression copy();
}
