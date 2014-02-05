package de.skuzzle.polly.dom;



public interface ASTExpression extends ASTPollyNode {

    public void resolveType();
    
    @Override
    public ASTExpression copy();
}