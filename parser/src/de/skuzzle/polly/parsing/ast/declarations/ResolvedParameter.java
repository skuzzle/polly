package de.skuzzle.polly.parsing.ast.declarations;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;

/**
 * Contains information about an actual call of a formal parameter. That is, the 
 * parameters formal type, its formal name and the actual expression that it was called
 * with.
 * 
 * @author Simon Taddiken
 *
 */
public class ResolvedParameter extends Parameter {

    private final Expression actual;
    
    
    /**
     * Creates a new ResolvedParameter.
     * 
     * @param position The position of the formal parameter.
     * @param name The name of the formal parameter.
     * @param actual The actual expression that this parameter is called with.
     */
    public ResolvedParameter(Position position, ResolvableIdentifier name, 
            Expression actual) {
        super(position, name, actual.getType());
        this.actual = actual;
    }
    
    
    
    /**
     * Gets the actual expression that this parameter is called with.
     * 
     * @return The expression.
     */
    public Expression getActual() {
        return this.actual;
    }
}
