package de.skuzzle.polly.parsing.ast.expressions;

import java.util.Arrays;
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
    
    /**
     * Creates a new OperatorCall to a binary expression which has two operands.
     * 
     * @param position Position of the call within the input.
     * @param operator The called operator.
     * @param left The left operand.
     * @param right The right operand.
     * @return The created Operator call.
     */
    public final static OperatorCall binary(Position position, OpType operator, 
            Expression left, Expression right) {
        return new OperatorCall(position, operator, 
            Arrays.asList(new Expression[] {left, right}));
    }
    
    
    
    /**
     * Creates a new OperatorCall to a unary expression which has one operand.
     * 
     * @param position Position of the call within the input.
     * @param operator The called operator.
     * @param operand The operand of the operator.
     * @return The created Operator call.
     */
    public final static OperatorCall unary(Position position, OpType operator, 
            Expression operand) {
        return new OperatorCall(position, operator, 
            Arrays.asList(new Expression[] {operand}));
    }
    
    
    
    /**
     * Creates a new OperatorCall to a ternary expression which has three operands.
     * 
     * @param position Position of the call within the input.
     * @param operator The called operator.
     * @param operand1 The first operand.
     * @param operand2 The second operand.
     * @param operand3 The third operand.
     * @return The created Operator call.
     */
    public final static OperatorCall ternary(Position position, OpType operator, 
            Expression operand1, Expression operand2, Expression operand3) {
        return new OperatorCall(position, operator, 
            Arrays.asList(new Expression[] {operand1, operand2, operand3}));
    }
    
    

    private final OpType operator;
    
    
    /**
     * Creates a new Operator call.
     * 
     * @param position Position of the call within the source.
     * @param operator The called operator.
     * @param parameters The actual parameters of the call.
     */
    private OperatorCall(Position position, OpType operator, 
            Collection<Expression> parameters) {
        super(position, 
            new VarAccess(position, new ResolvableIdentifier(position, operator.getId())),
            parameters);
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
