package de.skuzzle.polly.parsing.tree.literals;

import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.tree.Expression;



public abstract class Literal extends Expression implements Comparable<Literal> {

    private static final long serialVersionUID = 1L;
    
    private Token token;
    
    public Literal(Token token, Type type) {
        super(token.getPosition(), type);
        this.token = token;
    }
    
    
    public Token getToken() {
        return this.token;
    }
    
    
    
    public Literal castTo(Type target) throws ExecutionException {
        if (this.getType().check(target)) {
            return this;
        }
        Type.castError(this.getType(), target, this.getPosition());
        return null;
    }
    
    
    
    @Override
    public Expression contextCheck(Namespace context) 
            throws ParseException {
        return this;
    }


    
    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        stack.push(this);
    }
}
