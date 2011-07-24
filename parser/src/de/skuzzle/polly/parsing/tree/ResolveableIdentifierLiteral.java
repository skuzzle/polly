package de.skuzzle.polly.parsing.tree;

import java.util.Stack;

import de.skuzzle.polly.parsing.Context;
import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.Type;




public class ResolveableIdentifierLiteral extends IdentifierLiteral {

    private static final long serialVersionUID = 1L;
    private Expression resolvedExpression;
    
    
    public ResolveableIdentifierLiteral(Token token) {
        super(token);
    }
    
    
    public ResolveableIdentifierLiteral(java.lang.String string) {
        super(string);
    }
    
    
    private ResolveableIdentifierLiteral(Token token, Type type) {
        super(token);
        this.setType(type);
    }
    
    
    
    @Override
    public Expression contextCheck(Context context) 
            throws ParseException {
        this.resolvedExpression = context.resolveVar(this);
        return this.resolvedExpression;
    }
    
    
    
    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        this.resolvedExpression.collapse(stack);
    }
    
    
    
    @Override
    public int compareTo(Literal o) {
        if (o instanceof ResolveableIdentifierLiteral) {
            return this.getIdentifier().compareTo(
                    ((ResolveableIdentifierLiteral) o).getIdentifier());
        }
        throw new RuntimeException("Not compareable");
    }
    
    
    
    @Override
    public Object clone() {
        ResolveableIdentifierLiteral result = new ResolveableIdentifierLiteral(
                this.getToken(), this.getType());
        if (this.resolvedExpression != null) {
            result.resolvedExpression = (Expression) this.resolvedExpression.clone();
        }
        result.setPosition(this.getPosition());
        return result;
    }
}
