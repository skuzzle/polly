package de.skuzzle.polly.parsing.tree.operators;

import java.util.Stack;

import de.skuzzle.polly.parsing.Context;
import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ListType;
import de.skuzzle.polly.parsing.Parameter;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.tree.BooleanLiteral;
import de.skuzzle.polly.parsing.tree.Expression;
import de.skuzzle.polly.parsing.tree.FunctionDefinition;
import de.skuzzle.polly.parsing.tree.ListLiteral;
import de.skuzzle.polly.parsing.tree.Literal;
import de.skuzzle.polly.parsing.tree.TypeExpression;




public class ContainsFunction extends FunctionDefinition {
    
    private static final long serialVersionUID = 1L;

    public ContainsFunction() {
        super("contains", null, 
                new Parameter(new TypeExpression(new ListType(Type.ANY)), ";_list"), 
                new Parameter(new TypeExpression(Type.ANY), ";_number2"));
        this.setType(Type.BOOLEAN);
    }
    
    
    
    @Override
    public Expression contextCheck(Context context)
            throws ParseException {
        super.contextCheck(context);
        
        /*
         * List size checked by super.contextCheck
         */
        Expression list = this.actualParameters.get(0);
        Expression element = this.actualParameters.get(1);
        ListType type = (ListType) list.getType();
        
        if (!type.getSubType().check(element.getType())) {
            Type.typeError(element.getType(), type.getSubType(), 
                    element.getPosition());
        }
        return this;
    }

    

    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        Stack<Literal> s = new Stack<Literal>();
        this.actualParameters.get(0).collapse(s);
        this.actualParameters.get(1).collapse(s);
        
        Literal second = s.pop();
        ListLiteral first = (ListLiteral) s.pop();
        stack.push(new BooleanLiteral(first.getElements().contains(second)));
    }

    
    
    @Override
    public Object clone() {
        ContainsFunction result = new ContainsFunction();
        return result;
    }

}
