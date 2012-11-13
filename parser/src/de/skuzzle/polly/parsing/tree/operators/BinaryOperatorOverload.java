package de.skuzzle.polly.parsing.tree.operators;

import java.io.Serializable;
import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.TokenType;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.tree.Expression;
import de.skuzzle.polly.parsing.tree.literals.Literal;
import de.skuzzle.polly.parsing.types.Type;



public abstract class BinaryOperatorOverload implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;
    
    private TokenType operator;
    private Type leftExpected;
    private Type rightExpected;
    private Type returnType;
    
    protected Position leftExpression;
    protected Position rightExpression;
    
    

    public BinaryOperatorOverload(TokenType operator, Type leftExpected,
            Type rightExpected, Type returnValue) {
        this.operator = operator;
        this.leftExpected = leftExpected;
        this.rightExpected = rightExpected;
        this.returnType = returnValue;
    }

    
    
    public TokenType getOperatorType() {
        return this.operator;
    }
    

    
    public BinaryOperatorOverload match(TokenType operator2, Type left, Type right) {
        return this.operator.equals(operator2) 
               && this.leftExpected.check(left) 
               && this.rightExpected.check(right) ? this : null;
    }
    
    
    
    public void contextCheck(Namespace context, Expression left, 
            Expression right) throws ASTTraversalException {
        
        if (!left.getType().check(this.leftExpected)) {
            Type.typeError(left.getType(), this.leftExpected, left.getPosition());
        }
        
        if (!right.getType().check(this.rightExpected)) {
            Type.typeError(right.getType(), this.rightExpected, right.getPosition());
        }
        
        this.leftExpression = left.getPosition();
        this.rightExpression = right.getPosition();
    }
    
    
    
    public abstract void collapse(Stack<Literal> stack) throws ExecutionException;

    
    
    public Type getReturnType() {
        return this.returnType;
    }
    
    
    
    @Override
    public String toString() {
        return this.returnType + ": " + this.operator.toString() +
                "(" + this.leftExpected.toString() + 
                ", " + this.rightExpected.toString() + ")";
    }
    
    
    protected void setReturnType(Type type) {
        this.returnType = type;
    }
}
