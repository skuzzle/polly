package de.skuzzle.polly.parsing.ast.expressions.parameters;

import de.skuzzle.polly.parsing.ast.declarations.types.ListTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;


public class ResolvableListType implements ResolvableType {

    private final ResolvableType subType;
    
    
    public ResolvableListType(ResolvableType subType) {
        this.subType = subType;
    }
    
    
    
    @Override
    public Type resolve() throws ASTTraversalException {
        final Type subType = this.subType.resolve();
        return new ListTypeConstructor(subType);
    }
    
    
    
    @Override
    public String toString() {
        return "list<" + this.subType + ">";
    }
}
