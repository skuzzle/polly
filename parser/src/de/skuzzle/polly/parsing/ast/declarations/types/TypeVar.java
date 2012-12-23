package de.skuzzle.polly.parsing.ast.declarations.types;

import de.skuzzle.polly.parsing.ast.Identifier;


public class TypeVar extends Type {

    private static final long serialVersionUID = 1L;
    

    
    public TypeVar(Identifier name) {
        super(name, false, false);
    }

    
    
    @Override
    public Type fresh() {
        return Type.newTypeVar();
    }
    
    
    
    @Override
    public Type substitute(TypeVar var, Type t) {
        if (var == this) {
            return t;
        }
        return this;
    }
    
    
    
    @Override
    public String toString() {
        return this.getName().toString();
    }
    
    
    
    @Override
    public void visit(TypeVisitor visitor) {
        visitor.visitVar(this);
    }
}
