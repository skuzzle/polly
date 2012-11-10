package de.skuzzle.polly.parsing.ast.expressions.literals;

import de.skuzzle.polly.parsing.Position;


public class CommandLiteral extends StringLiteral {

    public CommandLiteral(Position position, String value) {
        super(position, value);
    }

}
