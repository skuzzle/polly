package de.skuzzle.polly.parsing.tree;

import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ListType;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.declarations.TypeDeclaration;
import de.skuzzle.polly.parsing.tree.literals.Literal;
import de.skuzzle.polly.parsing.tree.literals.ResolvableIdentifierLiteral;


public class TypeParameterExpression extends Expression {

    private static final long serialVersionUID = 1L;
    
    private ResolvableIdentifierLiteral mainType;
    private ResolvableIdentifierLiteral subType;
    
    
    
    public TypeParameterExpression(ResolvableIdentifierLiteral mainType,
            ResolvableIdentifierLiteral subType, Position position) {
        super(position);
        this.mainType = mainType;
        this.subType = subType;
    }
    
    
    
    public TypeParameterExpression(ResolvableIdentifierLiteral mainType, 
            Position position) {
        super(position);
        this.mainType = mainType;
    }



    @Override
    public Expression contextCheck(Namespace context) throws ParseException {
        TypeDeclaration main = context.resolveType(this.mainType);
        this.setType(main.getType());
        
        if (this.subType != null && main.getType() == Type.LIST) {
            TypeDeclaration sub = context.resolveType(this.subType);
            
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
                (ResolvableIdentifierLiteral) this.mainType.clone(), 
                this.getPosition());
        result.setType(this.getType());
        return result;
    }
}
