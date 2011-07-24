package de.skuzzle.polly.parsing.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import de.skuzzle.polly.parsing.Context;
import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;



/**
 * This class represents a function call within the syntax tree. It knows the functions
 * name and has a list of actual parameters. During the context analysis, the 
 * {@link FunctionDefinition} this call stands for is resolved and compared to the
 * actual parameters of this call. Afterward, this call holds the resulting expression of
 * the function definition.
 * 
 * @author Simon
 *
 */
public class FunctionCall extends Expression {

    private static final long serialVersionUID = 1L;
    
    private ResolveableIdentifierLiteral name;
    private List<Expression> actualParameters;
    private Expression resolvedExpression;
    
    
    
    public FunctionCall(ResolveableIdentifierLiteral name) {
        this(name, new ArrayList<Expression>());
        this.name = name;
    }
    
    
    
    public FunctionCall(ResolveableIdentifierLiteral name, 
            List<Expression> actualParameters) {
        super(name.getPosition());
        this.name = name;
        this.actualParameters = actualParameters;
    }
    
    
    
    public List<Expression> getActualParameters() {
        return this.actualParameters;
    }
    

    
    @Override
    public Expression contextCheck(Context context) throws ParseException {
        Expression e = this.name.contextCheck(context);
        
        if (!(e instanceof FunctionDefinition)) {
            throw new ParseException("'" + this.name.getIdentifier() + 
                    "' ist keine Funktion", this.getPosition());
        }
        
        FunctionDefinition func = (FunctionDefinition) e;
        
        List<Expression> checkedExpressions = new ArrayList<Expression>();
        for (Expression param : this.actualParameters) {
            checkedExpressions.add(param.contextCheck(context));
        }
        
        func.setActualParameters(checkedExpressions);
        this.resolvedExpression = func.contextCheck(context);
        
        this.setType(this.resolvedExpression.getType());
        return this;
    }

    
    
    /**
     * Executes this function call by executing the {@link #resolvedExpression}.
     * 
     * @param stack The stack used for execution.
     * @throws ExecutionException if a runtime error such as division by zero occurs. This
     *      can not be covered by {@link #contextCheck(Context)}.
     */
    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        this.resolvedExpression.collapse(stack);
    }
    
    
    
    /**
     * Performs a deep copy of this expression. The resulting call will have a deep 
     * copied list of actual parameters.
     * 
     * @return An identical FunctionCall.
     */
    @Override
    public Object clone() {
        List<Expression> params = new ArrayList<Expression>();
        for (Expression param : this.actualParameters) {
            params.add((Expression) param.clone());
        }
        FunctionCall call = new FunctionCall(
                (ResolveableIdentifierLiteral) this.name.clone(), params);
        call.name = (ResolveableIdentifierLiteral) this.name.clone();
        call.setType(this.getType());
        call.setPosition(this.getPosition());
        call.resolvedExpression = (Expression) this.resolvedExpression.clone();
        return call;
    }
}
