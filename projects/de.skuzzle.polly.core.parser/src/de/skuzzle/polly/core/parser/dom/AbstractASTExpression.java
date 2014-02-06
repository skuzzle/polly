package de.skuzzle.polly.core.parser.dom;

import de.skuzzle.polly.dom.ASTExpression;


public abstract class AbstractASTExpression extends AbstractPollyNode 
        implements ASTExpression {

    @Override
    public ASTExpression deepOrigin() {
        return super.deepOrigin().as(ASTExpression.class);
    }
    
    @Override
    public ASTExpression getOrigin() {
        return super.getOrigin().as(ASTExpression.class);
    }
}
