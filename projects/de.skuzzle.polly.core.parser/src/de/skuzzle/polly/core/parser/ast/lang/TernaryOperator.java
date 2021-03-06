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



public abstract class TernaryOperator<FIRST extends Literal, SECOND extends Literal, 
        THIRD extends Literal> extends Operator {

    private final static ResolvableIdentifier FIRST_PARAM_NAME = 
            new ResolvableIdentifier(Position.NONE, "$first");
    private final static ResolvableIdentifier SECOND_PARAM_NAME =
            new ResolvableIdentifier(Position.NONE, "$second");
    private final static ResolvableIdentifier THIRD_PARAM_NAME =
        new ResolvableIdentifier(Position.NONE, "$third");
    
    protected Type first;
    protected Type second;
    protected Type third;
    
    
    
    public TernaryOperator(OpType id) {
        super(id);
    }
    
    
    
    /**
     * Initializes the result- and operand types for this operator.
     * 
     * @param resulType The result type of this operator.
     * @param first Type of the first operand.
     * @param second Type of the second operand.
     * @param third Type of the third operand.
     */
    protected final void initTypes(Type resulType, Type first, Type second, Type third) {
        this.addType(resulType);
        this.setUnique(resulType);
        this.first = first;
        this.second = second;
        this.third = third;

    }
    
    
    
    @Override
    protected FunctionLiteral createFunction() {
        Collection<Declaration> p = Arrays.asList(new Declaration[] {
            this.typeToParameter(this.first, FIRST_PARAM_NAME),
            this.typeToParameter(this.second, SECOND_PARAM_NAME),
            this.typeToParameter(this.third, THIRD_PARAM_NAME),
        });
        
        final FunctionLiteral func = new FunctionLiteral(Position.NONE, p, this);
        func.setUnique(
            new ProductType(
                this.first, 
                this.second,
                this.third).mapTo(this.getUnique()));
        return func;
    }
    
    
    
    @Override
    @SuppressWarnings("unchecked")
    public final void execute(Stack<Literal> stack, Namespace ns, 
        ExecutionVisitor execVisitor) throws ASTTraversalException {
        
        final FIRST first = (FIRST) ns.resolveFirst(FIRST_PARAM_NAME).getExpression();
        final SECOND second = (SECOND) ns.resolveFirst(SECOND_PARAM_NAME).getExpression();
        final THIRD third = (THIRD) ns.resolveFirst(THIRD_PARAM_NAME).getExpression();
        
        final Position resultPos = Position.correctSpan(first.getPosition(), 
            second.getPosition(), third.getPosition());
        
        this.exec(stack, ns, first, second, third, resultPos, execVisitor);
    }
    
    
    
    /**
     * Called to generate the result of this operator on the stack.
     * 
     * @param stack The current execution stack.
     * @param ns The current execution namespace.
     * @param first First operand of this operator.
     * @param second Second operand of this operator.
     * @param third Third operand of this operator.
     * @param resultPos Position that can be used as position for the result literal.
     * @param execVisitor Current execution visitor.
     * @throws ASTTraversalException If executing fails for any reason.
     */
    protected abstract void exec(Stack<Literal> stack, Namespace ns, 
        FIRST first, SECOND second, THIRD third, Position resultPos, 
        ExecutionVisitor execVisitor) throws ASTTraversalException;
    
    
    
    @Override
    public final void resolveType(Namespace ns, AbstractTypeResolver typeResolver)
            throws ASTTraversalException {
        final Expression first = ns.resolveFirst(FIRST_PARAM_NAME).getExpression();
        final Expression second = ns.resolveFirst(SECOND_PARAM_NAME).getExpression();
        final Expression third = ns.resolveFirst(THIRD_PARAM_NAME).getExpression();
        
        this.resolve(first, second, third, ns, typeResolver);
    }
    
    
    
    /**
     * Called to context check this operator. Default implementation does nothing.
     * 
     * @param first The first operand of the operator call.
     * @param second The second operand of the operator call.
     * @param third The third operand of the operator call.
     * @param ns The current namespace.
     * @param typeResolver The {@link TypeResolver}.
     * @throws ASTTraversalException If context checking fails.
     */
    protected void resolve(Expression first, Expression second, Expression third, 
            Namespace ns, AbstractTypeResolver typeResolver) 
                throws ASTTraversalException {
        // empty
    }
}
