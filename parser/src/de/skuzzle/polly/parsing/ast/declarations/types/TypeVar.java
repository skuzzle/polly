package de.skuzzle.polly.parsing.ast.declarations.types;

import de.skuzzle.polly.parsing.ast.Identifier;

/**
 * Represents a type variable within a type expression. In order to resolve a proper 
 * substitute for a type variable, the type expression must be successfully unified with
 * another type expression. 
 * 
 * @author Simon Taddiken
 */
public class TypeVar extends Type {

    private static final long serialVersionUID = 1L;
    

    /**
     * Creates a new TypeVar with the given name.
     * 
     * @param name The name of the type var.
     */
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
