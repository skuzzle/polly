package de.skuzzle.polly.parsing.tree.operators;

import java.util.Stack;

import de.skuzzle.polly.parsing.Context;
import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ListType;
import de.skuzzle.polly.parsing.Parameter;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.tree.Expression;
import de.skuzzle.polly.parsing.tree.FunctionDefinition;
import de.skuzzle.polly.parsing.tree.ListLiteral;
import de.skuzzle.polly.parsing.tree.Literal;
import de.skuzzle.polly.parsing.tree.NumberLiteral;
import de.skuzzle.polly.parsing.tree.TypeExpression;




public class ListLengthFunction extends FunctionDefinition {
    
    private static final long serialVersionUID = 1L;
    
    public ListLengthFunction() {
        super("length", null, 
                new Parameter(new TypeExpression(new ListType(Type.ANY)), ";_list"));
        this.setType(Type.NUMBER);
    }
    
    
    
    @Override
    public Expression contextCheck(Context context)
            throws ParseException {
        super.contextCheck(context);

        return this;
    }
    
    

    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        Stack<Literal> s = new Stack<Literal>();
        this.actualParameters.get(0).collapse(s);
        ListLiteral first = (ListLiteral) s.pop();
        stack.push(new NumberLiteral(first.getElements().size(), this.getPosition()));
    }
    
    
    
    @Override
    public Object clone() {
        return new ListLengthFunction();
    }
}
