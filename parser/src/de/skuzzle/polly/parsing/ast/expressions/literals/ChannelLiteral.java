package de.skuzzle.polly.parsing.ast.expressions.literals;

import de.skuzzle.polly.parsing.Position;


public class ChannelLiteral extends StringLiteral {

    public ChannelLiteral(Position position, String value) {
        super(position, value);
    }

    
    
    @Override
    public String format(LiteralFormatter formatter) {
        return formatter.formatChannel(this);
    }
}
