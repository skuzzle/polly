package de.skuzzle.polly.parsing.tree;

import java.util.Stack;

import de.skuzzle.polly.parsing.Context;
import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;

/* This class was introduced to fix ISSUE: 0000003*/
public class VarAccessExpression extends Expression {

    private static final long serialVersionUID = 1L;
    
    private ResolveableIdentifierLiteral var;
    
    
    
    public VarAccessExpression(ResolveableIdentifierLiteral var, Position position) {
        super(position);
        this.var = var;
    }
    
    
    
    public ResolveableIdentifierLiteral getVar() {
        return this.var;
    }

    
    
    @Override
    public Expression contextCheck(Context context) throws ParseException {
        Expression e = this.var.contextCheck(context);
        
        if (e instanceof FunctionDefinition) {           
            throw new ParseException("Fehlende Klammern bei Funktionsaufruf", 
                    this.getPosition());
        }
        
        return e;
    }
    

    
    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        // do nothing as this expression will always be replaced.
    }
    
    

    @Override
    public Object clone() {
        return new VarAccessExpression(
                (ResolveableIdentifierLiteral) this.var.clone(), this.getPosition());
    }

}
