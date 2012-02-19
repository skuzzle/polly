package de.skuzzle.polly.parsing.tree;

import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;
import de.skuzzle.polly.parsing.tree.literals.Literal;
import de.skuzzle.polly.parsing.tree.literals.UserLiteral;



public class NamespaceAccessExpression extends Expression {
    
    private static final long serialVersionUID = 1L;
    
    private Expression namespace;
    private Expression rhs;
    
    
    
    public NamespaceAccessExpression(Expression namespace, 
            Expression rhs, Position position) {
        super(position);
        this.namespace = namespace;
        this.rhs = rhs;
    }
    
    

    @Override
    public Expression contextCheck(Namespace context) throws ParseException {       
        if (this.namespace instanceof VarOrCallExpression) {
            this.namespace = ((VarOrCallExpression) this.namespace).getId();
        }
        
        // now look up the namespace
        String ns = null;
        if (this.namespace instanceof UserLiteral) {
            ns = ((UserLiteral) this.namespace).getUserName();
        } else if (this.namespace instanceof IdentifierLiteral) {
            ns = ((IdentifierLiteral) this.namespace).getIdentifier();
        } else {
            throw new ParseException("Ungültiger Namespace Zugriff.", this.getPosition());
        }
        
        // Contextcheck the right hand expression with declarations of the selected 
        // namespace
        Namespace other = context.copyFor(ns);
        ((VarOrCallExpression) this.rhs).contextCheckForMember(other, context, true);
        
        this.setType(this.rhs.getType());
        return this;
    }
    
    

    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        this.rhs.collapse(stack);  
    }
    
    
    
    @Override
    public String toString() {
        return this.namespace.toString() + "." + this.rhs.toString();
    }
}
