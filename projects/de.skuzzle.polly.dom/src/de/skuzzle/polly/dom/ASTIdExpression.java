package de.skuzzle.polly.dom;


public interface ASTIdExpression extends ASTExpression {

    /**
     * Gets the name belonging to this id expression.
     * 
     * @return The name
     */
    public ASTName getName();
    
    @Override
    public ASTIdExpression copy();
}
