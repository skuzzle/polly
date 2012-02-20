package de.skuzzle.polly.parsing.tree;

import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.tree.literals.Literal;
import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;


public class CastExpression extends Expression {

    private static final long serialVersionUID = 1L;
    
    private Expression expression;
    private IdentifierLiteral castOp;
    
    public CastExpression(Expression expression, IdentifierLiteral castOp, 
            Position position) {
        super(position);
        this.expression = expression;
        this.castOp = castOp;
    }

    
    
    @Override
    public Expression contextCheck(Namespace context) throws ParseException {
        Expression target = this.castOp.contextCheck(context);
        
        if (!(target instanceof TypeExpression)) {
            // this was no cast but a normal identifier in braces. So return the resolved
            // expression
            return target;
        }
        
        this.expression = this.expression.contextCheck(context);
        this.setType(target.getType());
        return this;
        
    }
    
    

    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        this.expression.collapse(stack);
        Literal lit = stack.pop();
        
        lit = lit.castTo(this.getType());
        stack.push(lit);
    }
    
    
    
    @Override
    public String toString() {
        return "(" + this.castOp.toString() + ")" + this.expression;
    }
}
