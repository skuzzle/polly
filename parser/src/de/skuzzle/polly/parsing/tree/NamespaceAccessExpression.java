package de.skuzzle.polly.parsing.tree;

import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.tree.literals.Literal;
import de.skuzzle.polly.parsing.tree.literals.ResolvableIdentifierLiteral;
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
        if (this.namespace instanceof VarAccessExpression) {
            /*
             * If the left side of this expression is a var access, try to resolve it
             * as it might evaluate to a user 
             */
            VarAccessExpression vve = (VarAccessExpression) this.namespace;
            
            try {
                this.namespace = vve.contextCheck(context);
            } catch (ParseException e) {
                // if it could not be resolved, use the identifier as namespace name
                this.namespace = vve.getVar();
            }
        }
        
        // now look up the namespace
        String ns = null;
        if (this.namespace instanceof UserLiteral) {
            ns = ((UserLiteral) this.namespace).getUserName();
        } else if (this.namespace instanceof ResolvableIdentifierLiteral) {
            ns = ((ResolvableIdentifierLiteral) this.namespace).getIdentifier();
        } else {
            throw new ParseException("Ungültiger Namespace Zugriff.", this.getPosition());
        }
        
        // Contextcheck the right hand expression with declarations of the selected 
        // namespace
        try {
            context.switchTo(ns, this.getPosition());
            ((VarOrCall) this.rhs).contextCheckForMember(context);
        } finally {
            context.switchToRoot();
        }
        return this;
    }
    
    

    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        this.rhs.collapse(stack);  
    }
    
    

    @Override
    public Object clone() {
        NamespaceAccessExpression result = new NamespaceAccessExpression(
            (Expression) this.namespace.clone(), 
            (Expression)this.rhs.clone(), this.getPosition());
        return result;
    }
}
