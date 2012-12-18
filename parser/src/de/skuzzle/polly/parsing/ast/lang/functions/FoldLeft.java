package de.skuzzle.polly.parsing.ast.lang.functions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.parameters.Parameter;
import de.skuzzle.polly.parsing.ast.lang.Function;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.FunctionType;
import de.skuzzle.polly.parsing.types.ListType;
import de.skuzzle.polly.parsing.types.Type;
import de.skuzzle.polly.parsing.util.Stack;



/**
 * <p>Fold left function for list literals. It takes a list, a function and an argument. 
 * Then, the function starts with the argument as $result and subsequently stores the 
 * application of the function with parameters $result and the next list element back
 * to $result.</p>
 * 
 * <p>Type information: <code>A foldLeft(List&lt;A&gt;, \(A: A, B), A)</code></p>
 *   
 * @author Simon Taddiken
 */
public class FoldLeft extends Function {

    private static final long serialVersionUID = 1L;
    
    private final static ResolvableIdentifier FIRST_PARAM_NAME = 
        new ResolvableIdentifier(Position.NONE, "$list");
    private final static ResolvableIdentifier SECOND_PARAM_NAME =
        new ResolvableIdentifier(Position.NONE, "$operator");
    private final static ResolvableIdentifier THIRD_PARAM_NAME =
        new ResolvableIdentifier(Position.NONE, "$arg");
    
    
    
    private final Type firstOperand;
    private final Type secondOperand;
    private final Type thirdOperand;
    
    
    
    public FoldLeft() {
        super("foldLeft");
        this.firstOperand = ListType.ANY_LIST;
        this.secondOperand = new FunctionType(Type.ANY, 
            Arrays.asList(new Type[] { Type.ANY, Type.ANY }));
        this.thirdOperand = Type.ANY;
        
        this.setMustCopy(true);
    }


    
    @Override
    protected FunctionLiteral createFunction() {
        final Collection<Parameter> p = Arrays.asList(new Parameter[] {
            this.typeToParameter(this.firstOperand, FIRST_PARAM_NAME),
            this.typeToParameter(this.secondOperand, SECOND_PARAM_NAME),
            this.typeToParameter(this.thirdOperand, THIRD_PARAM_NAME)
        });
        
        final FunctionLiteral func = new FunctionLiteral(Position.NONE, p, this);
        func.setUnique(new FunctionType(Type.ANY, Arrays.asList(
            new Type[] { this.firstOperand, this.secondOperand, this.thirdOperand})));
        func.setReturnType(Type.ANY);
        
        return func;
    }
    
    
    
    @Override
    public void execute(Stack<Literal> stack, Namespace ns, Visitor execVisitor)
            throws ASTTraversalException {
        
        final ListLiteral list = (ListLiteral) ns.resolveVar(FIRST_PARAM_NAME, 
            Type.ANY).getExpression();
        final FunctionLiteral func = (FunctionLiteral) ns.resolveVar(SECOND_PARAM_NAME, 
            Type.ANY).getExpression();
        final Literal arg = (Literal) ns.resolveVar(THIRD_PARAM_NAME, 
            Type.ANY).getExpression();
        
        Literal result = arg;
        for (final Expression exp : list.getContent()) {
            final Call call = new Call(Position.NONE, func, 
                Arrays.asList(new Expression[] {result, exp}), Position.NONE);
            
            call.visit(execVisitor);
            result = stack.pop();
        }
        stack.push(result);
    }
    
    
    
    @Override
    public void resolveType(Namespace ns, Visitor typeResolver)
            throws ASTTraversalException {
        
        final Expression first = ns.resolveVar(FIRST_PARAM_NAME, 
            Type.ANY).getExpression();
        final Expression second = ns.resolveVar(SECOND_PARAM_NAME, 
            Type.ANY).getExpression();
        final Expression third = ns.resolveVar(THIRD_PARAM_NAME, 
            Type.ANY).getExpression();
        
        final ListType lt = (ListType) first.getUnique();
        final Type subType = lt.getSubType();
        
        final FunctionType ft = (FunctionType) second.getUnique();
        final Iterator<Type> paramIt = ft.getParameters().iterator();
        final Type param1 = paramIt.next();
        final Type param2 = paramIt.next();
        final Type argType = third.getUnique(); // type of the argument will be return type
        
        if (!ft.getReturnType().check(argType) || !param1.check(argType) || 
                !param2.check(subType)) {
            Type.typeError(ft.getReturnType(), argType, first.getPosition());
        }
        this.setUnique(argType);
    }

}
