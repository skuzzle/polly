package de.skuzzle.polly.parsing.tree.operators;

import java.util.Random;
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





public class RandomFunction extends FunctionDefinition {

    private static final long serialVersionUID = 1L;
    
    private Random randomizer;
    
    public RandomFunction() {
        super("random", null, 
                new Parameter(new TypeExpression(Type.NUMBER), ";_number1"), 
                new Parameter(new TypeExpression(Type.NUMBER), ";_number2"));
        this.setType(Type.NUMBER);
        this.randomizer = new Random();
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
        this.actualParameters.get(1).collapse(s);
        NumberLiteral second = (NumberLiteral) s.pop();
        NumberLiteral first = (NumberLiteral) s.pop();
        
        int start = first.isInteger();
        int end = second.isInteger();
        
        stack.push(new NumberLiteral(this.randomizer.nextInt(end - start + 1) + start, 
                this.getPosition()));
    }

    
    
    @Override
    public Object clone() {
        return new RandomFunction();
    }

}
