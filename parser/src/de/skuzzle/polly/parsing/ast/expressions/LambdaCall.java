package de.skuzzle.polly.parsing.ast.expressions;

import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;


public class LambdaCall extends Call {

    private FunctionLiteral lambda;
    
    
    protected LambdaCall(Position position, FunctionLiteral lambda, 
            Collection<Expression> parameters) {
        super(position, parameters);
        this.lambda = lambda;
    }
    
    
    
    public FunctionLiteral getLambda() {
        return this.lambda;
    }
    
    
    

    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitLambdaCall(this);
    }
}