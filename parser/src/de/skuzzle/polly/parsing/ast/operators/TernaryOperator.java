package de.skuzzle.polly.parsing.ast.operators;

import java.util.Arrays;
import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
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
import de.skuzzle.polly.parsing.util.Stack;



public abstract class TernaryOperator<FIRST extends Literal, SECOND extends Literal, 
        THIRD extends Literal> extends Operator {

    private static final long serialVersionUID = 1L;
    
    private final static ResolvableIdentifier FIRST_PARAM_NAME = 
            new ResolvableIdentifier(Position.EMPTY, "$first");
    private final static ResolvableIdentifier SECOND_PARAM_NAME =
            new ResolvableIdentifier(Position.EMPTY, "$second");
    private final static ResolvableIdentifier THIRD_PARAM_NAME =
        new ResolvableIdentifier(Position.EMPTY, "$third");
    
    private final Type first;
    private final Type second;
    private final Type third;
    
    
    
    public TernaryOperator(OpType id, Type resultType, Type first, Type second, 
            Type third) {
        super(id, resultType);
        this.first = first;
        this.second = second;
        this.third = third;
    }
    
    
    
    /**
     * Creates a {@link FunctionLiteral} which represents this operator. It will have two
     * formal parameters corresponding to this operators operand types.
     * 
     * @return A new FunctionLiteral.
     */
    protected FunctionLiteral createFunction() {
        Collection<Parameter> p = Arrays.asList(new Parameter[] {
            this.typeToParameter(this.first, FIRST_PARAM_NAME),
            this.typeToParameter(this.second, SECOND_PARAM_NAME),
            this.typeToParameter(this.third, THIRD_PARAM_NAME),
        });
        
        final FunctionLiteral func = new FunctionLiteral(Position.EMPTY, p, this);
        func.setType(new FunctionType(this.getType(), Arrays.asList(new Type[] {
            this.first, 
            this.second,
            this.third})));
        func.setReturnType(this.getType());
        
        return func;
    }
    
    
    
    @Override
    public Declaration createDeclaration() {
        final FunctionLiteral func = this.createFunction();
        final Identifier fakeId = new Identifier(Position.EMPTY, this.getOp().getId());
        final VarDeclaration vd = new VarDeclaration(func.getPosition(), fakeId, func);
        vd.setOperator(true);
        return vd;
    }
    
    
    
    @Override
    @SuppressWarnings("unchecked")
    public final void execute(Stack<Literal> stack, Namespace ns, 
            Visitor execVisitor) throws ASTTraversalException {
        
        final FIRST first= (FIRST) ns.resolveVar(FIRST_PARAM_NAME, 
            Type.ANY).getExpression();
        final SECOND second = (SECOND) ns.resolveVar(SECOND_PARAM_NAME, 
            Type.ANY).getExpression();
        final THIRD third = (THIRD) ns.resolveVar(THIRD_PARAM_NAME, 
            Type.ANY).getExpression();
        
        this.exec(stack, ns, first, second, third, 
            new Position(first.getPosition(), third.getPosition()));
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
     * @throws ASTTraversalException If executing fails for any reason.
     */
    protected abstract void exec(Stack<Literal> stack, Namespace ns, 
        FIRST first, SECOND second, THIRD third, Position resultPos) throws ASTTraversalException;
    
    
    
    @Override
    public final void resolveType(Namespace ns, Visitor typeResolver)
            throws ASTTraversalException {
        final Expression first = ns.resolveVar(FIRST_PARAM_NAME, 
            Type.ANY).getExpression();
        final Expression second = ns.resolveVar(SECOND_PARAM_NAME, 
            Type.ANY).getExpression();
        final Expression third = ns.resolveVar(THIRD_PARAM_NAME, 
            Type.ANY).getExpression();
        
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
            Namespace ns, Visitor typeResolver) throws ASTTraversalException {
        // empty
    }
}
