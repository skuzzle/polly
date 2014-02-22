package de.skuzzle.polly.dom;


public interface ASTDeclaration extends ASTPollyNode {

    /**
     * Gets the name of this declaration.
     * 
     * @return The delcaration's name.
     */
    public ASTName getName();
    
    
    @Override
    public ASTDeclaration getOrigin();
    
    @Override
    public ASTDeclaration deepOrigin();
    
    @Override
    public ASTDeclaration copy();
}
