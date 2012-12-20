package de.skuzzle.polly.parsing.ast.declarations.types;

import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.tools.Equatable;


public class TypeVar extends Type {

    private static final long serialVersionUID = 1L;
    
    private Type substitute;
    
    
    public TypeVar(Identifier name) {
        super(name, false, false);
    }
    
    
    
    protected void setSubstitute(Type t) {
        this.substitute = t;
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
    protected boolean canSubstitute(TypeVar var, Type type) {
        return var == this || this.substitute == null || this.substitute.equals(type);
    }
    
    
    
    @Override
    public boolean isUnifiableWith(Type other, boolean unify) 
            throws ASTTraversalException {
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
            if (unify) {
                next.substituteTypeVar(this, other);
                return true;
            }
            return next.canSubstitute(this, other);
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
        // vars that have not been substituted are equal to any type
        return this.substitute == null ? true : this.substitute.equals(o);
    }

    
    
    @Override
    public String toString() {
        return this.substitute == null 
            ? this.getName().toString() : this.substitute.toString();
    }
    
    
    
    @Override
    public void visit(TypeVisitor visitor) {
        visitor.visitVar(this);
    }
}
