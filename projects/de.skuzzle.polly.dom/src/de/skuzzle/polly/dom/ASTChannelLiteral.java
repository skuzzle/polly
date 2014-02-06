package de.skuzzle.polly.dom;


public interface ASTChannelLiteral extends ASTStringLiteral {

    @Override
    public ASTChannelLiteral getOrigin();
    
    @Override
    public ASTChannelLiteral deepOrigin();
    
    @Override
    public ASTStringLiteral copy();
}
