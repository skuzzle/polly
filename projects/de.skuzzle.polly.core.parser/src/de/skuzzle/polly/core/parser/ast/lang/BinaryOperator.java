package de.skuzzle.polly.core.parser.ast.lang;

import java.util.Arrays;
import java.util.Collection;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.ResolvableIdentifier;
import de.skuzzle.polly.core.parser.ast.declarations.Declaration;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.ProductType;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.core.parser.ast.visitor.resolving.AbstractTypeResolver;
import de.skuzzle.polly.tools.collections.Stack;


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

    private final static ResolvableIdentifier LEFT_PARAM_NAME = 
            new ResolvableIdentifier(Position.NONE, "$left");
    private final static ResolvableIdentifier RIGHT_PARAM_NAME =
            new ResolvableIdentifier(Position.NONE, "$right");
    
    protected Type left;
    protected Type right;
    
    
    
    /**
     * Creates a new binary operator.
     * 
     * @param id The type of the operator.
     */
    public BinaryOperator(OpType id) {
        super(id);
    }
    
    
    
    /**
     * Initializes the result- and operand types for this operator.
     * 
     * @param resultType The type of the value that this operator returns.
     * @param left Type of the left operand.
     * @param right Type of the right operand.
     */
    protected final void initTypes(Type resultType, Type left, Type right) {
        this.addType(resultType);
        this.setUnique(resultType);
        this.left = left;
        this.right = right;
    }
    
    
    
    @Override
    protected FunctionLiteral createFunction() {
        final Collection<Declaration> p = Arrays.asList(new Declaration[] {
            this.typeToParameter(this.left, LEFT_PARAM_NAME),
            this.typeToParameter(this.right, RIGHT_PARAM_NAME)
        });
        
        final FunctionLiteral func = new FunctionLiteral(Position.NONE, p, this);
        final ProductType source = new ProductType(
            this.left, this.right);
        func.setUnique(source.mapTo(this.getUnique()));
        return func;
    }
    
    
    
    @Override
    @SuppressWarnings("unchecked")
    public final void execute(Stack<Literal> stack, Namespace ns, 
        ExecutionVisitor execVisitor) throws ASTTraversalException {
        
        final L left = (L) ns.resolveFirst(LEFT_PARAM_NAME).getExpression();
        final R right = (R) ns.resolveFirst(RIGHT_PARAM_NAME).getExpression();
        
        final Position resultPos = Position.correctSpan(left.getPosition(), 
            right.getPosition());
        
        this.exec(stack, ns, left, right, resultPos, execVisitor);
    }
    
    
    
    @Override
    public final void resolveType(Namespace ns, AbstractTypeResolver typeResolver)
            throws ASTTraversalException {
        final Expression left = ns.resolveFirst(LEFT_PARAM_NAME).getExpression();
        final Expression right = ns.resolveFirst(RIGHT_PARAM_NAME).getExpression();
        
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
        AbstractTypeResolver typeResolver) throws ASTTraversalException {
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
     * @param execVisitor Current execution visitor.
     * @throws ASTTraversalException If executing fails for any reason.
     */
    protected abstract void exec(Stack<Literal> stack, Namespace ns, 
        L left, R right, Position resultPos, ExecutionVisitor execVisitor) 
            throws ASTTraversalException;
}