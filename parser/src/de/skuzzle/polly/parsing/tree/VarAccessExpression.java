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
        
        return decl.getExpression().contextCheck(context);
    }
    

    
    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        // do nothing as this expression will always be replaced.
    }
    
    

    @Override
    public Object clone() {
        return new VarAccessExpression(
                (ResolvableIdentifierLiteral) this.var.clone(), this.getPosition());
    }

}
