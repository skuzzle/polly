package de.skuzzle.polly.core.parser.ast.declarations.types;

import de.skuzzle.polly.core.parser.ast.Identifier;

/**
 * Represents a type variable within a type expression. In order to resolve a proper 
 * substitute for a type variable, the type expression must be successfully unified with
 * another type expression. 
 * 
 * @author Simon Taddiken
 */
public class TypeVar extends Type {

    protected TypeVar source;
    
    /**
     * Creates a new TypeVar with the given name.
     * 
     * @param name The name of the type var.
     */
    public TypeVar(Identifier name) {
        super(name, false, false);
    }
    
    
    
    @Override
    public Type subst(Substitution s) {
        return s.getSubstitute(this);
    }
    
    
    
    @Override
    public String toString() {
        if (this.source != null && this.source != this) {
            return this.getName().toString() + " [" + this.source.toString() + "]";
        }
        return this.getName().toString();
    }
    
    
    
    @Override
    public boolean visit(TypeVisitor visitor) {
        return visitor.visit(this);
    }
}
