package de.skuzzle.polly.parsing.tree.literals;

import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.tree.Expression;



public class ResolvableIdentifierLiteral extends IdentifierLiteral {

    private static final long serialVersionUID = 1L;
    private VarDeclaration declaration;
    
    
    public ResolvableIdentifierLiteral(Token token) {
        super(token);
    }
    
    
    public ResolvableIdentifierLiteral(String string) {
        super(string);
    }
    
    public ResolvableIdentifierLiteral(IdentifierLiteral id) {
        super(id.getToken());
    }
    
    
    private ResolvableIdentifierLiteral(Token token, Type type) {
        super(token);
        this.setType(type);
    }
    
    
    
    @Override
    public Expression contextCheck(Namespace context) 
            throws ParseException {
        this.declaration = context.resolveVar(this);
        return this.declaration.getExpression();
    }
    
    
    
    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        // Do nothing as this Identifier will always be replaced by its declaration
        // during context check
        //this.declaration.getExpression().collapse(stack);
    }
    
    
    
    @Override
    public int compareTo(Literal o) {
        if (o instanceof ResolvableIdentifierLiteral) {
            return this.getIdentifier().compareTo(
                    ((ResolvableIdentifierLiteral) o).getIdentifier());
        }
        throw new RuntimeException("Not compareable");
    }
}
