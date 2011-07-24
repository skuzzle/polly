package de.skuzzle.polly.parsing.tree;

import java.util.Stack;

import de.skuzzle.polly.parsing.Context;
import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Type;


public class TypeExpression extends Expression {

    private static final long serialVersionUID = 1L;

    public TypeExpression(Type type) {
        super(Position.EMPTY, type);
    }
    

    @Override
    public Expression contextCheck(Context context) throws ParseException {
        return this;
    }

    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        // do nothing;
    }
    
    
    
    @Override
    public Object clone() {
        TypeExpression result = new TypeExpression(this.getType());
        result.setPosition(this.getPosition());
        return result;
    }
}
