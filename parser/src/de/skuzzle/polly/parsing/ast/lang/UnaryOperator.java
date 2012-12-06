package de.skuzzle.polly.parsing.ast.lang;

import java.util.Arrays;
import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.TypeResolver;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.FunctionType;
import de.skuzzle.polly.parsing.types.Type;
import de.skuzzle.polly.parsing.util.Stack;


public abstract class UnaryOperator<O extends Literal> extends Operator {

    private static final long serialVersionUID = 1L;
    protected final static ResolvableIdentifier PARAM_NAME = 
            new ResolvableIdentifier(Position.EMPTY, "$param");
    private final Type operandType;
    
    
    
    public UnaryOperator(OpType op, Type resultType, Type operandType) {
        super(op, resultType);
        this.operandType = operandType;
    }
    
    
    
    @Override
    protected FunctionLiteral createFunction() {
        Collection<Parameter> p = Arrays.asList(new Parameter[] {
            this.typeToParameter(this.operandType, PARAM_NAME)});
        
        final FunctionLiteral func = new FunctionLiteral(Position.EMPTY, p, this);
        func.setType(new FunctionType(this.getType(), Parameter.asType(p)));
        func.setReturnType(this.getType());
        return func;
    }
    
    
    
    @Override
    public final void resolveType(Namespace ns, Visitor typeResolver)
            throws ASTTraversalException {
        final Expression param = ns.resolveVar(PARAM_NAME, 
            Type.ANY).getExpression();
        
        this.resolve(param, ns, typeResolver);
    }
    
    
    
    /**
     * Called to context check this operator. Default implementation does nothing.
     * 
     * @param param The operand of the operator call.
     * @param ns The current namespace.
     * @param typeResolver The {@link TypeResolver}.
     * @throws ASTTraversalException If context checking fails.
     */
    protected void resolve(Expression param, Namespace ns, 
            Visitor typeResolver) throws ASTTraversalException {
        // empty
    }

    
    
    @Override
    @SuppressWarnings("unchecked")
    public void execute(Stack<Literal> stack, Namespace ns, Visitor execVisitor)
            throws ASTTraversalException {
        final O operand = (O) ns.resolveVar(PARAM_NAME, 
            Type.ANY).getExpression();
        
        this.exec(stack, ns, operand, operand.getPosition());
    }

    
    
    /**
     * Called to generate the result of this operator on the stack.
     * 
     * @param stack The current execution stack.
     * @param ns The current execution namespace.
     * @param operand Operand literal of this operator.
     * @param resultPos Position that can be used as position for the result literal.
     * @throws ASTTraversalException If executing fails for any reason.
     */
    protected abstract void exec(Stack<Literal> stack, Namespace ns, 
        O operand, Position resultPos) throws ASTTraversalException;
}