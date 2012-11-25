package de.skuzzle.polly.parsing.ast.declarations;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.util.CopyTool;

/**
 * Hardcoded declarations are merely the same as a normal {@link VarDeclaration} but they 
 * will always return a copy of the internal expression when calling 
 * {@link #getExpression()}. 
 * 
 * @author Simon Taddiken
 */
public class HardcodedDeclaration extends VarDeclaration {
    
    public HardcodedDeclaration(Position position, Identifier name, 
            Expression expression) {
        super(position, name, expression);
    }
    
    
    
    /**
     * Gets a deep copy of the internal stored expression that was supplied using
     * this constructor.
     * 
     * @return A copy of the declared expression.
     */
    @Override
    public Expression getExpression() {
        return super.getExpression();
    }
}
