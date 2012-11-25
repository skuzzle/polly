package de.skuzzle.polly.parsing.ast.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Stack;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.HardcodedDeclaration;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.TypeResolver;
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

    private final static String LEFT_PARAM_NAME = "$left";
    private final static String RIGHT_PARAM_NAME = "$right";
    
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
    
    
    
    /**
     * Creates a {@link FunctionLiteral} which represents this operator. It will have two
     * formal parameters corresponding to this operators operand types.
     * 
     * @return A new FunctionLiteral.
     */
    protected FunctionLiteral createFunction() {
        Collection<Parameter> p = Arrays.asList(new Parameter[] {
            this.typeToParameter(this.left, LEFT_PARAM_NAME),
            this.typeToParameter(this.right, RIGHT_PARAM_NAME)
        });
        
        final FunctionLiteral func = new FunctionLiteral(Position.EMPTY, p, this);
        func.setType(new FunctionType(this.getType(), Arrays.asList(new Type[] {
            this.left, 
            this.right})));
        func.setReturnType(this.getType());
        
        return func;
    }
    
    
    
    @Override
    public Declaration createDeclaration() {
        final FunctionLiteral func = this.createFunction();
        final Identifier fakeId = new Identifier(Position.EMPTY, this.getOp().getId());
        final VarDeclaration vd = new VarDeclaration(func.getPosition(), fakeId, func); 
        return vd;
    }
    
    
    
    @Override
    @SuppressWarnings("unchecked")
    public final void execute(Stack<Literal> stack, Namespace ns, 
            Visitor execVisitor) throws ASTTraversalException {
        
        final R right = (R) ns.resolveVar(
            new ResolvableIdentifier(this.getPosition(), LEFT_PARAM_NAME), 
            Type.ANY).getExpression();
        final L left = (L) ns.resolveVar(
            new ResolvableIdentifier(this.getPosition(), RIGHT_PARAM_NAME), 
            Type.ANY).getExpression();
        
        this.exec(stack, ns, left, right, 
            new Position(left.getPosition(), right.getPosition()));
    }
    
    
    
    @Override
    public final void resolveType(Namespace ns, Visitor typeResolver)
            throws ASTTraversalException {
        final Expression right = ns.resolveVar(
            new ResolvableIdentifier(this.getPosition(), LEFT_PARAM_NAME), 
            Type.ANY).getExpression();
        final Expression left = ns.resolveVar(
            new ResolvableIdentifier(this.getPosition(), RIGHT_PARAM_NAME), 
            Type.ANY).getExpression();
        
        this.resolve(left, right, ns, typeResolver);
    }
    
    
    
    /**
     * Called to context check this operator. Default implementation does nothing.
     * 
     * @param left The left operand of the operator call.
     * @param right The right operand of the operator call.
     * @param ns The current namespace.
     * @param typeResolver The {@link TypeResolver}.
     * @throws ASTTraversalException If context checking fails.
     */
    protected void resolve(Expression left, Expression right, Namespace ns, 
            Visitor typeResolver) throws ASTTraversalException {
        // empty
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
    protected abstract void exec(Stack<Literal> stack, Namespace ns, 
        L left, R right, Position resultPos) throws ASTTraversalException;
}