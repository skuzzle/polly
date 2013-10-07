package de.skuzzle.polly.core.parser.ast.directives;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.TokenType;
import de.skuzzle.polly.core.parser.ast.Node;


public abstract class Directive extends Node implements Comparable<Directive> {

    private final TokenType type;
    
    public Directive(Position position, TokenType type) {
        super(position);
        this.type = type;
    }

    
    
    public TokenType getDirectiveType() {
        return this.type;
    }
    
    
    
    @Override
    public int compareTo(Directive o) {
        return this.type.compareTo(o.type);
    }
}
