package de.skuzzle.polly.parsing.ast.expressions.literals;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;


public class ChannelLiteral extends StringLiteral {

    private static final long serialVersionUID = 1L;
    
    
    public ChannelLiteral(Position position, String value) {
        super(position, value, Type.CHANNEL);
    }

    
    
    @Override
    public Literal castTo(Type type) throws ASTTraversalException {
        if (type.equals(Type.STRING)) {
            return new StringLiteral(this.getPosition(), this.getValue());
        }
        return super.castTo(type);
    }
    
    
    
    @Override
    public String format(LiteralFormatter formatter) {
        return formatter.formatChannel(this);
    }
}
