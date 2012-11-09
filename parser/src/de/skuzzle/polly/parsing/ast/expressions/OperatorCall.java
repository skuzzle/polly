package de.skuzzle.polly.parsing.ast.expressions;

import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.operators.Operator;
import de.skuzzle.polly.parsing.ast.operators.Operator.OpType;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;


/**
 * Represents a call of an operator.
 *  
 * @author Simon Taddiken
 */
public class OperatorCall extends Call {

    private final OpType operator;
    
    
    /**
     * Creates a new Operator call.
     * 
     * @param position Position of the call within the source.
     * @param operator The called operator.
     * @param parameters The actual parameters of the call.
     */
    public OperatorCall(Position position, OpType operator, 
            Collection<Expression> parameters) {
        super(position, new ResolvableIdentifier(position, operator.getId()), parameters);
        this.operator = operator;
    }


    
    /**
     * Gets the operator type that is being called.
     * 
     * @return The operator type.
     */
    public Operator.OpType getOperator() {
        return this.operator;
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitOperatorCall(this);
    }
}
