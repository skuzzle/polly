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



public abstract class TernaryOperatorOverload implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;
    
    private TokenType operator;
    private Type firstExpected;
    private Type secondExpected;
    private Type thirdExpected;
    private Type returnType;
    
    protected Position firstExpression;
    protected Position secondExpression;
    protected Position thirdExpression;
    
    

    public TernaryOperatorOverload(TokenType operator, Type firstExpected,
            Type secondExpected, Type thirdExpected, Type returnType) {
        this.operator = operator;
        this.firstExpected = firstExpected;
        this.secondExpected = secondExpected;
        this.thirdExpected = thirdExpected;
        this.returnType = returnType;
    }

    
    
    public TokenType getOperatorType() {
        return this.operator;
    }
    

    
    public TernaryOperatorOverload match(TokenType operator, Type first, 
            Type second, Type third) {
        return this.operator.equals(operator)
               && this.firstExpected.check(first) 
               && this.secondExpected.check(second) 
               && this.thirdExpected.check(third) ? this : null;
    }
    
    
    
    public void contextCheck(Namespace context, Expression first, Expression second, 
            Expression third) throws ParseException {
        
        if (!first.getType().check(this.firstExpected)) {
            Type.typeError(first.getType(), this.firstExpected, first.getPosition());
        }
        
        if (!second.getType().check(this.secondExpected)) {
            Type.typeError(second.getType(), this.secondExpected, second.getPosition());
        }
        
        if (!third.getType().check(this.thirdExpected)) {
            Type.typeError(third.getType(), this.thirdExpected, third.getPosition());
        }
        
        this.firstExpression = first.getPosition();
        this.secondExpression = second.getPosition();
        this.thirdExpression = third.getPosition();
    }
    
    
    
    public abstract void collapse(Stack<Literal> stack) throws ExecutionException;
    
    @Override
    public abstract Object clone();
    
    
    public Type getReturnType() {
        return this.returnType;
    }
    
    
    
    protected void setReturnType(Type type) {
        this.returnType = type;
    }
}
