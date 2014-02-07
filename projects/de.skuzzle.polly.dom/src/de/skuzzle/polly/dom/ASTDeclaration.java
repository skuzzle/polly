package de.skuzzle.polly.dom;


public interface ASTDeclaration extends ASTPollyNode {

    /**
     * Gets the name of the declaration.
     * 
     * @return The name.
     */
    public ASTName getName();
    
    /**
     * Gets the body of this declaration.
     * 
     * @return The body.
     */
    public ASTExpression getBody();
    
    /**
     * Sets the provided expression as body of this declaration.
     * 
     * @param body The new body.
     */
    public void setBody(ASTExpression body);
    
    @Override
    public ASTDeclaration getOrigin();
    
    @Override
    public ASTDeclaration deepOrigin();
    
    @Override
    public ASTDeclaration copy();
}
