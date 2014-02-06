package de.skuzzle.polly.dom;



public interface ASTExpression extends ASTPollyNode {

    public void resolveType();
    
    @Override
    public ASTExpression deepOrigin();
    
    @Override
    public ASTExpression getOrigin();
    
    @Override
    public ASTExpression copy();
}