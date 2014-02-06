package de.skuzzle.polly.dom;


public interface ASTLiteral extends ASTExpression {

    /**
     * Gets the value of this literal
     * @return The value.
     */
    public Object getValue();
    
    @Override
    public ASTLiteral getOrigin();
    
    public ASTLiteral deepOrigin();
    
    @Override
    public ASTLiteral copy();
}
