package de.skuzzle.polly.parsing.tree;

import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.TokenType;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.tree.literals.Literal;
import de.skuzzle.polly.parsing.tree.operators.TernaryOperatorOverload;



/**
 * This is a ternary expression. That means it takes three arguments (operands) and 
 * applies an operator on them. It has a {@link Position} which spans the whole
 * expression and a type which is resolved during context analysis using the
 * first-, second and third operands types and the operator.
 * 
 * @author Simon
 *
 */
public class TernaryExpression extends Expression {

    private static final long serialVersionUID = 1L;

    /**
     * The first operand for this expression.
     */
    private Expression firstOperand;
    
    /**
     * The second operand for this expression.
     */
    private Expression secondOperand;
    
    /**
     * The third operand for this expression.
     */
    private Expression thirdOperand;
    
    /**
     * The operator for this expression as {@link TokenType}.
     */
    private Token operator;
    
    /**
     * The operator to be applied to the operands. This will be resolved using the
     * {@link #operator} and the types of the operands.
     */
    private TernaryOperatorOverload overload;
    
    
    
    /**
     * Creates a new ternary expression with the given expressions as operands. The
     * position of the new expression reaches from the beginning of the first expression
     * until the end of the <b>second</b> expression. The type of the new expression is 
     * UNKNOWN until {@link #contextCheck(Context)} is run.
     * 
     * @param firstOperand The first operand of the expression.
     * @param secondOperand The second operand of the expression.
     * @param thirdOperand The third operand of the expression.
     * @param operator The operator for this expression.
     */
    public TernaryExpression(Expression firstOperand, Expression secondOperand, 
            Expression thirdOperand, Token operator) {

        super(new Position(firstOperand.getPosition(), secondOperand.getPosition()), 
                Type.UNKNOWN);
        
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.thirdOperand = thirdOperand;
        this.operator = operator;
    }
    
    
   
    /**
     * Sets the third operand for this expression and updates this expressions position
     * to span until the end of the third operand.
     * 
     * @param expression The third operand for this expression.
     */
    public void setThirdOperand(Expression expression) {
        this.thirdOperand = expression;
        this.setPosition(new Position(this.firstOperand.getPosition(), 
                expression.getPosition()));
    }
    
    
    
    /**
     * Runs the context analysis on this expression. First, all three operands 
     * are replaced by their contextCheck result.Next, the operator overload is
     * determined by comparing all three operands types and the {@link #operator}.
     * Then, contextCheck is run on the resolved {@link TernaryOperatorOverload} to 
     * resolve the return type of this expression.
     * 
     * @param context The context in which this tree element is checked.
     * @return This TernaryExpression.
     * @throws ParseException if any context error such as type-mismatch occur during
     *      context checking.
     */
    @Override
    public Expression contextCheck(Namespace context) throws ParseException {
        this.firstOperand = this.firstOperand.contextCheck(context);
        this.secondOperand = this.secondOperand.contextCheck(context);
        this.thirdOperand = this.thirdOperand.contextCheck(context);
        
        
        this.overload = context.resolveOperator(this.operator.getType(), 
                this.firstOperand.getType(), this.secondOperand.getType(),
                this.thirdOperand.getType(), this.getPosition());
        
        this.overload.contextCheck(context, this.firstOperand, 
                this.secondOperand, this.thirdOperand);
        this.setType(this.overload.getReturnType());
        
        return this;
    }

    
    
    /**
     * Executes this expression by first executing all three operands (which all store
     * their result on the stack) and then executing the operator which pops the three
     * topmost stack items and pushes back his result.
     * 
     * @param stack The stack used for execution.
     * @throws ExecutionException if a runtime error such as division by zero occurs. This
     *      can not be covered by {@link #contextCheck(Context)}.
     */
    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        this.firstOperand.collapse(stack);
        this.secondOperand.collapse(stack);
        this.thirdOperand.collapse(stack);
        this.overload.collapse(stack);
    }
    
    
    
    @Override
    public String toString() {
        // TODO: string representation of ternary expression
        return "TODO";
    }
}
