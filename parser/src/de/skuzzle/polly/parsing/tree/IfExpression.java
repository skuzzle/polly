package de.skuzzle.polly.parsing.tree;

import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.tree.literals.BooleanLiteral;
import de.skuzzle.polly.parsing.tree.literals.Literal;
import de.skuzzle.polly.parsing.types.Type;


public class IfExpression extends Expression {


    private static final long serialVersionUID = 1L;
    
    private Expression condition;
    private Expression ifExpression;
    private Expression elseExpression;
    
    
    
    public IfExpression(Expression condition, Expression ifExpression, 
            Expression elseExpression) {
        super(condition.getPosition());
        this.condition = condition;
        this.ifExpression = ifExpression;
        this.elseExpression = elseExpression;
    }
    
    

    @Override
    public Expression contextCheck(Namespace context) throws ParseException {
        this.condition = this.condition.contextCheck(context);
        
        if (!this.condition.getType().check(Type.BOOLEAN)) {
            Type.typeError(this.condition.getType(), Type.BOOLEAN, 
                this.condition.getPosition());
        }
        
        this.ifExpression = this.ifExpression.contextCheck(context);
        this.elseExpression = this.elseExpression != null 
            ? this.elseExpression.contextCheck(context) 
            : null;
            
        this.setType(this.ifExpression.getType());
        
        if (this.elseExpression != null && 
            this.getType().check(this.elseExpression.getType())) {
            
            Type.typeError(this.elseExpression.getType(), this.getType(), 
                this.elseExpression.getPosition());
        }
        
        return this;
    }

    
    
    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        this.condition.collapse(stack);
        BooleanLiteral condition = (BooleanLiteral) stack.pop();
        
        if (condition.getValue()) {
            this.ifExpression.collapse(stack);
        } else if (this.elseExpression != null) {
            this.elseExpression.collapse(stack);
        }
    }

}
