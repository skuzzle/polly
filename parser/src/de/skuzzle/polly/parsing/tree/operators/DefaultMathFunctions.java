package de.skuzzle.polly.parsing.tree.operators;

import java.util.Stack;

import de.skuzzle.polly.parsing.Context;
import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.Parameter;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.tree.Expression;
import de.skuzzle.polly.parsing.tree.FunctionDefinition;
import de.skuzzle.polly.parsing.tree.Literal;
import de.skuzzle.polly.parsing.tree.NumberLiteral;
import de.skuzzle.polly.parsing.tree.TypeExpression;


public class DefaultMathFunctions extends FunctionDefinition {

    private static final long serialVersionUID = 1L;

    public DefaultMathFunctions(String name) {
        super(name, null, new Parameter(new TypeExpression(Type.NUMBER), ";_number"));
        this.setType(Type.NUMBER);
    }

    
    
    @Override
    public Expression contextCheck(Context context) throws ParseException {
        super.contextCheck(context);
        return this;
    }


    
    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        Stack<Literal> s = new Stack<Literal>();
        this.actualParameters.get(0).collapse(s);
        NumberLiteral operand = (NumberLiteral) s.pop();
        
        if (this.getName().getIdentifier().equals("sin")) {
            stack.push(new NumberLiteral(Math.sin(operand.getValue()), 
                    this.getPosition()));
        } else if (this.getName().getIdentifier().equals("cos")) {
            stack.push(new NumberLiteral(Math.cos(operand.getValue()), 
                    this.getPosition()));
        } else if (this.getName().getIdentifier().equals("tan")) {
            stack.push(new NumberLiteral(Math.tan(operand.getValue()), 
                    this.getPosition()));
        } else if (this.getName().getIdentifier().equals("abs")) {
            stack.push(new NumberLiteral(Math.abs(operand.getValue()), 
                    this.getPosition()));
        } else if (this.getName().getIdentifier().equals("sqrt")) {
            stack.push(new NumberLiteral(Math.sqrt(operand.getValue()), 
                    this.getPosition()));
        } else if (this.getName().getIdentifier().equals("ceil")) {
            stack.push(new NumberLiteral(Math.ceil(operand.getValue()), 
                    this.getPosition()));
        } else if (this.getName().getIdentifier().equals("floor")) {
            stack.push(new NumberLiteral(Math.floor(operand.getValue()), 
                    this.getPosition()));
        } else if (this.getName().getIdentifier().equals("log")) {
            stack.push(new NumberLiteral(Math.log(operand.getValue()), 
                    this.getPosition()));
        } else if (this.getName().getIdentifier().equals("round")) {
            stack.push(new NumberLiteral(Math.round(operand.getValue()), 
                    this.getPosition()));
        }
    }

    

    @Override
    public Object clone() {
        return new DefaultMathFunctions(this.getName().getIdentifier());
    }
}
