package de.skuzzle.polly.core.parser;

import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;

public class ParseException extends ASTTraversalException {

    private static final long serialVersionUID = 1L;

    
    public ParseException(String error, Position position) {
        super(position, error);
    }
}
