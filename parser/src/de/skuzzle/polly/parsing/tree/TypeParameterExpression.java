package de.skuzzle.polly.parsing.tree;

import java.util.Stack;

import de.skuzzle.polly.parsing.Context;
import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ListType;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Type;


public class TypeParameterExpression extends Expression {

    private static final long serialVersionUID = 1L;
    
    private ResolveableIdentifierLiteral mainType;
    private ResolveableIdentifierLiteral subType;
    
    
    
    public TypeParameterExpression(ResolveableIdentifierLiteral mainType,
            ResolveableIdentifierLiteral subType, Position position) {
        super(position);
        this.mainType = mainType;
        this.subType = subType;
    }
    
    
    
    public TypeParameterExpression(ResolveableIdentifierLiteral mainType, 
            Position position) {
        super(position);
        this.mainType = mainType;
    }



    @Override
    public Expression contextCheck(Context context) throws ParseException {
        Expression main = this.mainType.contextCheck(context);
        this.setType(main.getType());
        
        if (this.subType != null && main.getType() == Type.LIST) {
            Expression sub = this.subType.contextCheck(context);
            
            this.setType(new ListType(sub.getType()));
            
        } else if (this.subType != null) {
            throw new ParseException("Nur Listen können Untertypen haben!", 
                    this.getPosition());
        }
        
        return this;
    }



    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {

    }
    
    
    @Override
    public Object clone() {
        TypeParameterExpression result = new TypeParameterExpression(
                (ResolveableIdentifierLiteral) this.mainType.clone(), 
                this.getPosition());
        result.setType(this.getType());
        return result;
    }
}
