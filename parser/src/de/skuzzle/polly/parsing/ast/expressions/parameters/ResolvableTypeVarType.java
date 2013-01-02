package de.skuzzle.polly.parsing.ast.expressions.parameters;

import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;


public class ResolvableTypeVarType implements ResolvableType {

    private final Type type;
    
    
    public ResolvableTypeVarType() {
        this.type = Type.newTypeVar();
    }
    
    
    
    @Override
    public Type resolve() throws ASTTraversalException {
        return this.type;
    }
    
    
    
    @Override
    public String toString() {
        return this.type.getName().getId();
    }
}
