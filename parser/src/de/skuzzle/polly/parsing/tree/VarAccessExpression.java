package de.skuzzle.polly.parsing.tree;

import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.tree.literals.Literal;
import de.skuzzle.polly.parsing.tree.literals.ResolvableIdentifierLiteral;

/* This class was introduced to fix ISSUE: 0000003*/
public class VarAccessExpression extends Expression {

    private static final long serialVersionUID = 1L;
    
    private ResolvableIdentifierLiteral var;
    private Expression resolved;
    
    
    public VarAccessExpression(ResolvableIdentifierLiteral var, Position position) {
        super(position);
        this.var = var;
    }
    
    
    
    public ResolvableIdentifierLiteral getVar() {
        return this.var;
    }

    
    
    @Override
    public Expression contextCheck(Namespace context) throws ParseException {
        VarDeclaration decl = context.resolveVar(this.var);
        
        this.resolved = decl.getExpression().contextCheck(context);
        this.setType(resolved.getType());
        return this;
    }
    

    
    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        this.resolved.collapse(stack);
    }
    
    

    @Override
    public Object clone() {
        return new VarAccessExpression(
                (ResolvableIdentifierLiteral) this.var.clone(), this.getPosition());
    }

}
