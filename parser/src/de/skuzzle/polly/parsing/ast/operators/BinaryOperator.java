package de.skuzzle.polly.parsing.ast.operators;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.types.Type;


/**
 * Superclass for binary operators. The {@link #createDeclaration()} method is 
 * pre-implemented to return a matching declaration for this operator.
 * 
 * @author Simon Taddiken
 * @param <L> Literal type of the left operand of this operator.
 * @param <R> Literal type of the right operand of this operator.
 */
public abstract class BinaryOperator<L extends Literal, R extends Literal> extends Operator {

    private final Type left;
    private final Type right;
    
    
    
    /**
     * Creates a new binary operator.
     * 
     * @param id The type of the operator.
     * @param resultType The type of the value that this operator returns.
     * @param left Type of the left operand.
     * @param right Type of the right operand.
     */
    public BinaryOperator(OpType id, Type resultType, Type left, Type right) {
        super(id, resultType);
        this.left = left;
        this.right = right;
    }
    
    
    
    @Override
    public FunctionDeclaration createDeclaration() {
        Collection<Parameter> p = Arrays.asList(new Parameter[] {
            new Parameter(Position.EMPTY, this.left, 
                new Identifier(Position.EMPTY, "left")),
            new Parameter(Position.EMPTY, this.right, 
                new Identifier(Position.EMPTY, "left"))});
        
        final FunctionDeclaration result = new FunctionDeclaration(
            Position.EMPTY, 
            new Identifier(Position.EMPTY, this.getId().getId()), this, p);
        return result;
    }
    
    
    
    @Override
    @SuppressWarnings("unchecked")
    public void execute(LinkedList<Literal> stack, Namespace ns) {
        final R right = (R) stack.pop();
        final L left = (L) stack.pop();
        
        this.exec(stack, ns, left, right, 
            new Position(left.getPosition(), right.getPosition()));
    }
    
    
    
    /**
     * Called to generate the result of this operator on the stack.
     * 
     * @param stack The current execution stack.
     * @param ns The current execution namespace.
     * @param left Left operand of this operator.
     * @param right Right operand of this operator.
     * @param resultPos Position that can be used as position for the result literal.
     */
    protected abstract void exec(LinkedList<Literal> stack, Namespace ns, 
        L left, R right, Position resultPos);
}