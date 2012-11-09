package de.skuzzle.polly.parsing.ast.declarations;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;


public class ResolvedParameter extends Parameter {

    private final Expression actual;
    
    
    public ResolvedParameter(Position position, ResolvableIdentifier name, 
            Expression actual) {
        super(position, actual.getType(), name);
        this.actual = actual;
    }
    
    
    
    
    public Expression getActual() {
        return this.actual;
    }
}
