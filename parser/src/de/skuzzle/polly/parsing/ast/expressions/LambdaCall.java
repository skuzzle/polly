package de.skuzzle.polly.parsing.ast.expressions;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;


public class LambdaCall extends Call {

    private final static AtomicInteger lambdaIds = new AtomicInteger();
    
    /**
     * Creates a unique identifier used for Lambda-calls.
     * 
     * @param pos Source location of the call.
     * @return Unique identifier.
     */
    public final static Identifier getLambdaId(Position pos) {
        return new Identifier(pos, "$lmbd_" + lambdaIds.getAndIncrement());
    }
    
    
    
    private FunctionLiteral lambda;
    
    
    public LambdaCall(Position position, FunctionLiteral lambda, 
            Collection<Expression> parameters) {
        super(position, new ResolvableIdentifier(getLambdaId(position)), parameters);
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