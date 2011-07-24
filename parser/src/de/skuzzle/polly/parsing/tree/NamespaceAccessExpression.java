package de.skuzzle.polly.parsing.tree;

import java.util.Stack;

import de.skuzzle.polly.parsing.Context;
import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;



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
    public Expression contextCheck(Context context) throws ParseException {
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
        ResolveableIdentifierLiteral ns = null;
        if (this.namespace instanceof UserLiteral) {
            ns = new ResolveableIdentifierLiteral(
                ((UserLiteral) this.namespace).getUserName());
        } else if (this.namespace instanceof ResolveableIdentifierLiteral) {
            ns = (ResolveableIdentifierLiteral) this.namespace;
        } else {
            throw new ParseException("Ungültiger Namespace Zugriff.", this.getPosition());
        }
        
        ns.setPosition(this.namespace.getPosition());
        context.switchNamespace(ns);
        this.rhs = this.rhs.contextCheck(context);
        context.switchDefaultNamespace();
        return this.rhs;
    }
    
    

    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        // do nothing. This expression will alway be replaced        
    }
    
    

    @Override
    public Object clone() {
        NamespaceAccessExpression result = new NamespaceAccessExpression(
            (Expression) this.namespace.clone(), 
            (Expression)this.rhs.clone(), this.getPosition());
        return result;
    }
}
