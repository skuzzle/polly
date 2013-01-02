package de.skuzzle.polly.parsing.ast.expressions.parameters;

import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;


public class ResolvablePrimitiveType implements ResolvableType {

    private final ResolvableIdentifier typeName;
    
    
    public ResolvablePrimitiveType(ResolvableIdentifier typeName) {
        this.typeName = typeName;
    }
    
    
    
    @Override
    public Type resolve() throws ASTTraversalException {
        return Type.resolve(this.typeName);
    }
    
    
    
    @Override
    public String toString() {
        return this.typeName.getId();
    }
}
