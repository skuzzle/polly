package de.skuzzle.polly.parsing.ast.lang;

import java.util.Arrays;
import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.MapTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.ProductTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.parameters.Parameter;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.ast.visitor.resolving.TypeResolver;
import de.skuzzle.polly.parsing.util.Stack;


public abstract class UnaryOperator<O extends Literal> extends Operator {

    private static final long serialVersionUID = 1L;
    
    
    protected final static ResolvableIdentifier PARAM_NAME = 
            new ResolvableIdentifier(Position.NONE, "$param");
    
    private Type operandType;
    
    
    public UnaryOperator(OpType op) {
        super(op);
    }
    
    
    
    /**
     * Initializes the result- and operand types for this operator.
     * 
     * @param resultType The type of the value that this operator returns.
     * @param operandType Type of the operand.
     */
    protected final void initTypes(Type resultType, Type operandType) {
        this.setUnique(resultType);
        this.operandType = operandType;
    }
    
    
    
    @Override
    protected FunctionLiteral createFunction() {
        Collection<Parameter> p = Arrays.asList(new Parameter[] {
            this.typeToParameter(this.operandType, PARAM_NAME)});
        
        final FunctionLiteral func = new FunctionLiteral(Position.NONE, p, this);
        func.setUnique(
            new MapTypeConstructor(new ProductTypeConstructor(this.operandType), 
                this.getUnique()));
        return func;
    }
    
    
    
    @Override
    public final void resolveType(Namespace ns, Visitor typeResolver)
            throws ASTTraversalException {
        final Expression param = ns.resolveHere(PARAM_NAME).getExpression();
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
        final O operand = (O) ns.resolveHere(PARAM_NAME).getExpression();
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