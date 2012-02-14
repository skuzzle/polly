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
        if (this.namespace instanceof VarOrCallExpression) {
            this.namespace = ((VarOrCallExpression) this.namespace).getId();
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
        // TODO: The selected namespace should not contain local variables 
        //       because fuck you thats why
        try {
            context.switchTo(ns, this.getPosition());
            ((VarOrCallExpression) this.rhs).contextCheckForMember(context);
        } finally {
            context.switchToRoot();
        }
        
        this.setType(this.rhs.getType());
        return this;
    }
    
    

    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        this.rhs.collapse(stack);  
    }
}
