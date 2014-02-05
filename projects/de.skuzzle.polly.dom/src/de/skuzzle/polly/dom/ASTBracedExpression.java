package de.skuzzle.polly.dom;

import de.skuzzle.parser.dom.ASTNodeProperty;




public interface ASTBracedExpression extends ASTExpression {

    /** Property for children of this node */
    public final static ASTNodeProperty BRACED_EXPRESSION = 
            new ASTNodeProperty("BRACED_EXPRESSION");
    
    
    
    /**
     * Returns the expression in braces.
     * 
     * @return The expression in braces.
     */
    public ASTExpression getBracedExpression();
    
    /**
     * Sets the expression in braces.
     * 
     * @param expression The expression to set.
     */
    public void setBracedExpression(ASTExpression expression);
    
    @Override
    public ASTBracedExpression copy();
}
