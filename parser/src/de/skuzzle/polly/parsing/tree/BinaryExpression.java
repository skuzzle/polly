package de.skuzzle.polly.parsing.tree;

import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.tree.literals.Literal;
import de.skuzzle.polly.parsing.tree.operators.BinaryOperatorOverload;




/**
 * This is a binary expression. That means it takes two arguments (operands)
 * and applies an operator on them. It has a {@link Position} which spans the whole
 * expression and a type which is resolved during context analysis using the
 * left- and right operands types and the operator.
 * 
 * @author Simon
 *
 */
public class BinaryExpression extends Expression {

    private static final long serialVersionUID = 1L;
    
    /**
     * The left operand of this expression.
     */
    protected Expression leftOperand;
    
    /**
     * The operator for this expression as token type.
     */
    protected Token operator;
    
    /**
     * The right operand of this expression.
     */
    protected Expression rightOperand;
    
    /**
     * The operator to be applied to the operands. This will be resolved using the
     * {@link #operator} and the types of the two operands.
     */
    private BinaryOperatorOverload overload;
    
    
    
    /**
     * Creates a new BinaryExpression with all needed information. The new 
     * BinaryExpression gets a {@link Position} which spans the whole Expression. That
     * means it reaches from the beginning of the left operand to the end of the right 
     * operand.
     * 
     * The type of this expression is UNKNOWN until {@link #contextCheck(Context)} is run.
     * 
     * @param leftOperand The left operand.
     * @param operator The operator.
     * @param rightOperand The right operand.
     */
    public BinaryExpression(Expression leftOperand, Token operator, 
            Expression rightOperand) {

        super(new Position(leftOperand.getPosition(), rightOperand.getPosition()), 
                Type.UNKNOWN);
        
        this.leftOperand = leftOperand;
        this.operator = operator;
        this.rightOperand = rightOperand;
    }

    
    
    /**
     * Runs the context analysis on this expression. First the left-, then the 
     * right operands are replaced by their contextCheck result which resolves
     * their type.
     * Next, the operator overload is resolved by using the left- and the right operands
     * types and the {@link #operator}. Then, contextCheck is run on the 
     * {@link BinaryOperatorOverload} to resolve the return type of this expression.
     * 
     * @param context The context in which this tree element is checked.
     * @return This BinaryExpression.
     * @throws ParseException if any context error such as type-mismatch occur during
     *      context checking.
     */
    @Override
    public Expression contextCheck(Namespace context) throws ParseException {
        this.leftOperand = this.leftOperand.contextCheck(context);
        this.rightOperand = this.rightOperand.contextCheck(context);
        
        
        this.overload = context.resolveOperator(this.operator.getType(), 
                this.leftOperand.getType(), this.rightOperand.getType(), 
                this.getPosition());
        
        this.overload.contextCheck(context, this.leftOperand, this.rightOperand);
        this.setType(this.overload.getReturnType());
        
        return this;
    }

    
    
    /**
     * Executes this expression by executing the left- and the right operand (which
     * both store their results on the stack) and then executing the operator overload
     * which stores its result back on the stack.
     * 
     * @param stack The stack used for execution.
     * @throws ExecutionException if a runtime error such as division by zero occurs. This
     *      can not be covered by {@link #contextCheck(Context)}.
     */
    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        this.leftOperand.collapse(stack);
        this.rightOperand.collapse(stack);
        this.overload.collapse(stack);
    }
    
    
    
    /**
     * Performs a deep copy of this expression. The resulting expression will have the
     * same type and position and their operands are full copies of this expressions
     * operands.
     * 
     * @return An identical BinaryExpression.
     */
    @Override
    public Object clone() {
        BinaryExpression be =  new BinaryExpression(
                (Expression) this.leftOperand.clone(), this.operator, 
                (Expression) this.rightOperand.clone());
        
        be.setType(this.getType());
        be.setPosition(this.getPosition());
        if (this.overload != null) {
            be.overload = (BinaryOperatorOverload) this.overload.clone();
        }
        return be;
    }
}
