package de.skuzzle.polly.parsing.ast.expressions.parameters;

import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;


public interface ResolvableType {

    public Type resolve() throws ASTTraversalException;
    
}