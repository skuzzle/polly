package de.skuzzle.polly.parsing.ast.expressions.literals;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;


public class UserLiteral extends StringLiteral {

    private static final long serialVersionUID = 1L;
    
    public UserLiteral(Position position, String value) {
        super(position, value, Type.USER);
    }

    
    
    @Override
    public String format(LiteralFormatter formatter) {
        return formatter.formatUser(this);
    }
}
