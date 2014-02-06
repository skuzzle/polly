package de.skuzzle.polly.dom;


public interface ASTStringLiteral extends ASTLiteral {

    @Override
    public String getValue();

    /**
     * Sets the string value of this literal
     * @param value The new string value
     */
    public void setValue(String value);

    @Override
    public ASTStringLiteral getOrigin();
    
    @Override
    public ASTLiteral deepOrigin();
    
    @Override
    public ASTStringLiteral copy();
}
