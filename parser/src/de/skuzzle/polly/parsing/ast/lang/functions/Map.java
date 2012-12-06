package de.skuzzle.polly.parsing.ast.lang.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.lang.Function;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.FunctionType;
import de.skuzzle.polly.parsing.types.ListType;
import de.skuzzle.polly.parsing.types.Type;
import de.skuzzle.polly.parsing.util.Stack;


public class Map extends Function {

    private static final long serialVersionUID = 1L;
    
    private final static ResolvableIdentifier FIRST_PARAM_NAME = 
        new ResolvableIdentifier(Position.EMPTY, "$list");
    private final static ResolvableIdentifier SECOND_PARAM_NAME =
        new ResolvableIdentifier(Position.EMPTY, "$operator");
    
    private final Type firstOperand;
    private final Type secondOperand;
    
    
    
    public Map() {
        super("map");
        this.firstOperand = ListType.ANY_LIST;
        this.secondOperand = new FunctionType(Type.ANY, Arrays.asList(Type.ANY));
        
        this.setMustCopy(true);
    }
    
    

    @Override
    protected FunctionLiteral createFunction() {
        final Collection<Parameter> p = Arrays.asList(new Parameter[] {
            this.typeToParameter(this.firstOperand, FIRST_PARAM_NAME),
            this.typeToParameter(this.secondOperand, SECOND_PARAM_NAME),
        });
        
        final FunctionLiteral func = new FunctionLiteral(Position.EMPTY, p, this);
        func.setType(new FunctionType(Type.ANY, Arrays.asList(
            new Type[] { this.firstOperand, this.secondOperand})));
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
        
        final ArrayList<Expression> result = new ArrayList<Expression>();
        for (final Expression exp : list.getContent()) {
            final Call call = new Call(Position.EMPTY, func, 
                Arrays.asList(new Expression[] {exp}), Position.EMPTY);
            
            call.visit(execVisitor);
            result.add(stack.pop());
        }
        
        stack.push(new ListLiteral(list.getPosition(), result));
    }
    
    
    
    @Override
    public void resolveType(Namespace ns, Visitor typeResolver)
            throws ASTTraversalException {
        final Expression first = ns.resolveVar(
                FIRST_PARAM_NAME, Type.ANY).getExpression();
        final Expression second = ns.resolveVar(
            SECOND_PARAM_NAME, Type.ANY).getExpression();
        
        
        
        final Type subType = ((ListType) first.getType()).getSubType();
        final FunctionType ft = (FunctionType) second.getType();
        final Type paramType = ft.getParameters().iterator().next();
        
        if (!subType.check(paramType)) {
            Type.typeError(paramType, subType, second.getPosition());
        }
        
        this.setType(new FunctionType(ft.getReturnType(), Arrays.asList(
            new Type[] { subType })));
    }
    

}
