package de.skuzzle.polly.parsing.tree.operators;

import java.io.Serializable;
import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.TokenType;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.tree.Expression;
import de.skuzzle.polly.parsing.tree.literals.Literal;



public abstract class UnaryOperatorOverload implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;
    
    private TokenType operator;
    private Type expected;
    private Type returnType;
    
    protected Position expression;
    
    

    public UnaryOperatorOverload(TokenType operator, Type expected, Type returnValue) {
        this.operator = operator;
        this.expected = expected;
        this.returnType = returnValue;
    }

    
    
    public TokenType getOperatorType() {
        return this.operator;
    }
    

    
    public UnaryOperatorOverload match(TokenType operator, Type expected) {
        return this.operator.equals(operator) && 
            this.expected.check(expected) ? this : null;
    }
    
    
    
    public void contextCheck(Namespace context, 
            Expression expression) throws ParseException {
        
        if (!expression.getType().check(this.expected)) {
            Type.typeError(expression.getType(), this.expected, expression.getPosition());
        }
        
        this.expression = expression.getPosition();
    }
    
    
    
    public abstract void collapse(Stack<Literal> stack) throws ExecutionException;
    
    
    
    public Type getReturnType() {
        return this.returnType;
    }
    
    
    
    protected void setReturnType(Type returnType) {
        this.returnType = returnType;
    }
    
    
    
    @Override
    public String toString() {
        return this.returnType + ": " + this.operator.toString() +
                "(" + this.expected.toString() + ")";
    }
    
    
    
    @Override
    public abstract Object clone();

}