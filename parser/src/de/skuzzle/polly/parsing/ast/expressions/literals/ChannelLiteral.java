package de.skuzzle.polly.parsing.ast.expressions.literals;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.types.Type;


public class ChannelLiteral extends StringLiteral {

    public ChannelLiteral(Position position, String value) {
        super(position, value);
        this.setType(Type.CHANNEL);
    }

    
    
    @Override
    public Literal castTo(Type type) throws ASTTraversalException {
        if (type.check(Type.STRING)) {
            return new StringLiteral(this.getPosition(), this.getValue());
        }
        return super.castTo(type);
    }
    
    
    
    @Override
    public String format(LiteralFormatter formatter) {
        return formatter.formatChannel(this);
    }
}
