package de.skuzzle.polly.parsing;

import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;

public class ParseException extends ASTTraversalException {

    private static final long serialVersionUID = 1L;

    
    public ParseException(String error, Position position) {
        super(position, error);
    }
}
