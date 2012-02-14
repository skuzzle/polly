package de.skuzzle.polly.parsing.tree;

import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.TokenType;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.tree.literals.Literal;
import de.skuzzle.polly.parsing.tree.operators.UnaryOperatorOverload;



/**
 * This is a unary expression. It only has one parameter (operand) and applies an
 * operator to it. It has a {@link Position} which spans the whole expression (if its
 * a prefix expression) and a type which is resolved during context analysis.
 * 
 * @author Simon
 *
 */
public class UnaryExpression extends Expression {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * The operator for this expression as {@link TokenType}
     */
    private Token operator;
    
    /**
     * The operator to be applied to the operands. This will be resolved using the
     * {@link #operator} and the type of the operand.
     */
    private UnaryOperatorOverload overload;
    
    /**
     * The only operand for this expression.
     */
    private Expression operand;
    
    
    
    /**
     * Creates a new UnaryExpression with a given operator and operand. The type of the
     * newly created expression is UNKNOWN and its position spans from the beginning of
     * the operator until the end of the operand. Thus its representing a prefix 
     * operation because it always starts with an operator.
     * 
     * @param operator The operand for this expression.
     * @param operand The operator.
     */
    public UnaryExpression(Token operator, Expression operand) {
        super(new Position(operator.getPosition(), operand.getPosition()),
                operand.getType());
        this.operator = operator;
        this.operand = operand;
    }
    
    
    
    /**
     * Runs the context analysis on this expression by running it on its operand to
     * resolve the operands type. Afterwards, the {@link UnaryOperatorOverload} is
     * resolved using the operands type and the operator of this expression. Then,
     * contextCheck is run on the overload to resolve this expressions type.
     * 
     * @param context The context in which this tree element is checked.
     * @return This UnaryExpression.
     * @throws ParseException if any context error such as type-mismatch occur during
     *      context checking.
     */
    @Override
    public Expression contextCheck(Namespace context) throws ParseException {
        this.operand = this.operand.contextCheck(context);
        
        this.overload = context.resolveOperator(this.operator.getType(), 
                this.operand.getType(), this.getPosition());
        this.overload.contextCheck(context, this.operand);
        this.setType(this.overload.getReturnType());
        
        return this;
    }

    
    
    /**
     * Executes this expression by executing the {@link #operand} which stores one
     * item on top of the stack and then executing the operator overload which replaces
     * the topmost item with a result of the operator being applied to it.
     * 
     * @param stack The stack used for execution.
     * @throws ExecutionException if a runtime error such as division by zero occurs. This
     *      can not be covered by {@link #contextCheck(Context)}.
     */
    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        this.operand.collapse(stack);
        this.overload.collapse(stack);
    }
}
