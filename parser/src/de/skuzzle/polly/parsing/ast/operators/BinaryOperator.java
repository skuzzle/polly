package de.skuzzle.polly.parsing.ast.operators;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.FunctionType;
import de.skuzzle.polly.parsing.types.Type;


/**
 * Superclass for binary operators. The {@link #createDeclaration()} method is 
 * pre-implemented to return a matching declaration for this operator.
 * 
 * @author Simon Taddiken
 * @param <L> Literal type of the left operand of this operator.
 * @param <R> Literal type of the right operand of this operator.
 */
public abstract class BinaryOperator<L extends Literal, R extends Literal> 
        extends Operator {

    private final Type left;
    private final Type right;
    
    private final String leftParamName;
    private final String rightParamName;
    
    
    
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
        
        // create unique parameter names 
        this.leftParamName = getParamName();
        this.rightParamName = getParamName();
    }
    
    
    
    @Override
    public Declaration createDeclaration() {
        Collection<Parameter> p = Arrays.asList(new Parameter[] {
            new Parameter(Position.EMPTY, 
                new ResolvableIdentifier(Position.EMPTY, this.leftParamName), 
                this.left),
            new Parameter(Position.EMPTY, 
                new ResolvableIdentifier(Position.EMPTY, this.rightParamName), 
                this.right)});
        
        final FunctionLiteral func = new FunctionLiteral(Position.EMPTY, p, this);
        func.setType(new FunctionType(this.getType(), Parameter.asType(p)));
        
        final Identifier fakeId = new Identifier(Position.EMPTY, this.getOp().getId());
        return new VarDeclaration(func.getPosition(), fakeId, func);
    }
    
    
    
    @Override
    @SuppressWarnings("unchecked")
    public final void execute(LinkedList<Literal> stack, Namespace ns, 
            Visitor execVisitor) throws ASTTraversalException {
        
        final R right = (R) ns.resolveVar(
            new ResolvableIdentifier(this.getPosition(), this.leftParamName), 
            Type.ANY).getExpression();
        final L left = (L) ns.resolveVar(
            new ResolvableIdentifier(this.getPosition(), this.rightParamName), 
            Type.ANY).getExpression();
        
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
     * @throws ASTTraversalException If executing fails for any reason.
     */
    protected abstract void exec(LinkedList<Literal> stack, Namespace ns, 
        L left, R right, Position resultPos) throws ASTTraversalException;
}