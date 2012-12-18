package de.skuzzle.polly.parsing.ast.declarations.types;

import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.tools.Equatable;


public class TypeVar extends Type {

    private static final long serialVersionUID = 1L;
    
    private static int ids = 0;
    public static TypeVar create() {
        final Identifier id = new Identifier("$_" + (ids++));
        return new TypeVar(id);
    }
    
    
    
    private Type substitute;
    
    
    private TypeVar(Identifier name) {
        super(name, false, false);
    }

    
    
    @Override
    protected void substituteTypeVar(TypeVar var, Type type) 
            throws ASTTraversalException {
        if (var == this) {
            return;
        }
        
        if (var.getName().equals(this.getName())) {
            if (this.substitute != null) {
                throw new ASTTraversalException(var.getName().getPosition(), 
                    "Typvariable '" + this.getName() + "' bereits mit dem Typ '" + 
                    this.substitute.getName() + "' belegt.");
            }
            this.substitute = type;
        }
    }
    
    
    
    @Override
    public boolean isUnifiableWith(Type other) throws ASTTraversalException {
        if (this.substitute != null) {
            // if this typevar already represents a concrete type, expressions are only
            // unifiable if the substitute equals the given type.
            return this.substitute.equals(other);
        } else {
            // this typevar does not already represent a type, so we have found a valid
            // substitute for it. We then propagate this new substitution to all other 
            // TypeVars with identical names within the expression.
            this.substitute = other;
            
            // find root, then propagate substitution from there 
            Type next = this;
            for(; next.parent != null; next = next.parent);
            next.substituteTypeVar(this, other);
            return true;
        }
    }
    
    
    
    @Override
    public Class<?> getEquivalenceClass() {
        return Type.class;
    }
    
    
    
    @Override
    public boolean actualEquals(Equatable o) {
        if (o instanceof TypeVar) {
            TypeVar tv = (TypeVar) o;
            return this.getName().equals(tv.getName());
        }
        return this.substitute != null && this.substitute.equals(o);
    }

}
