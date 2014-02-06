package de.skuzzle.polly.dom;

import de.skuzzle.parser.dom.ASTNodeProperty;


public interface ASTIdExpression extends ASTExpression {

    /** Property for children of this node */
    public final static ASTNodeProperty ID_EXPRESSION_NAME = 
            new ASTNodeProperty("ID_EXPRESSION_NAME");
    
    /**
     * Gets the name belonging to this id expression.
     * 
     * @return The name
     */
    public ASTName getName();
    
    /**
     * Sets the name for this id expression.
     * @param name The new name. Must not be <code>null</code>.
     */
    public void setName(ASTName name);
    
    @Override
    public ASTIdExpression getOrigin();
    
    @Override
    public ASTIdExpression deepOrigin();
    
    @Override
    public ASTIdExpression copy();
}
