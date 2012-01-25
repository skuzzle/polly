package de.skuzzle.polly.parsing.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.tree.literals.Literal;
import de.skuzzle.polly.parsing.tree.literals.ResolvableIdentifierLiteral;



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
    
    private ResolvableIdentifierLiteral name;
    private List<Expression> actualParameters;
    private Expression resolvedExpression;
    private boolean hardcoded;
    
    
    public FunctionCall(ResolvableIdentifierLiteral name) {
        this(name, new ArrayList<Expression>());
        this.name = name;
    }
    
    
    
    public FunctionCall(ResolvableIdentifierLiteral name, 
            List<Expression> actualParameters) {
        super(name.getPosition());
        this.name = name;
        this.actualParameters = actualParameters;
    }
    
    
    
    public List<Expression> getActualParameters() {
        return this.actualParameters;
    }
    

    
    @Override
    public Expression contextCheck(Namespace context) throws ParseException {
        FunctionDeclaration decl = context.resolveFunction(this.name);
        
        if (decl.getFormalParameters().size() != this.actualParameters.size()) {
            throw new ParseException("Falsche Parameteranzahl: " + 
                    this.actualParameters.size() + ". Erwartet: " + 
                    decl.getFormalParameters().size(), this.getPosition());
        }

        context.enter();
        try {
            for (int i = 0; i < this.actualParameters.size(); ++i) {
                Expression actual = this.actualParameters.get(i);
                VarDeclaration formal = decl.getFormalParameters().get(i);
                
                actual = actual.contextCheck(context);
                this.actualParameters.set(i, actual);
                
                if (!formal.getType().check(actual.getType())) {
                    Type.typeError(actual.getType(), formal.getType(), 
                        actual.getPosition());
                }
                
                // declare a new var for each formal parameter which contains
                // the expression of the actual parameter
                VarDeclaration act = new VarDeclaration(formal.getName(), false, false);
                act.setExpression(actual);
                context.add(act);
            }
            
            this.hardcoded = decl.isHardcoded();
            
            // For non hardcoded functions, this will cause all parameters in the
            // expression to be replaced by their actual expression
            this.resolvedExpression = decl.getExpression().contextCheck(context);
        } finally {
            // make sure to leave the declarations in a clean state
            context.leave();
        }
        
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
        /* 
         * if this is a hardcoded function, put all actual parameters onto the stack.
         * 
         * Parameters of normal functions will be resolved during context check and 
         * replaced by heir actual expression
         */
        if (this.hardcoded) {
            for (Expression exp : this.actualParameters) {
                exp.collapse(stack);
            }
        }
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
                (ResolvableIdentifierLiteral) this.name.clone(), params);
        call.name = (ResolvableIdentifierLiteral) this.name.clone();
        call.setType(this.getType());
        call.setPosition(this.getPosition());
        call.resolvedExpression = (Expression) this.resolvedExpression.clone();
        return call;
    }
}
