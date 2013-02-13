package de.skuzzle.polly.parsing.ast.declarations.types;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class Substitution {

    /** Unmodifiable empty substitution. */
    public final static Substitution EMPTY = new Substitution(
        Collections.<TypeVar, Type>emptyMap());

    
    
    /** 
     * Creates a substitution that will replace all variables with new ones.
     * 
     * @return A new substitution.
     */
    public final static Substitution fresh() {
        return new Substitution() {
            @Override
            public Type getSubstitute(TypeVar v) {
                Type te = this.mappings.get(v);
                if (te == null) {
                    te = Type.newTypeVar();
                    this.mappings.put(v, te);
                }
                return te;
            }
        };
    }
    
    

    /** Substitution mappings in this instance */
    protected final Map<TypeVar, Type> mappings;
    
    
    
    public Substitution(Map<TypeVar, Type> mappings) {
        this.mappings = mappings;
    }
    
    
    
    public Substitution() {
        this(new HashMap<TypeVar, Type>());
    }
    
    
    
    /**
     * Applies the given substitution to this one, creating a new {@link Substitution}
     * instance. 
     * 
     * @param s The substitution to apply.
     * @return A new {@link Substitution} instance.
     */
    public Substitution subst(Substitution s) {
        final Substitution result = new Substitution(
            new HashMap<TypeVar, Type>());
        for (final Entry<TypeVar, Type> e : this.mappings.entrySet()) {
            final Type substitute = s.getSubstitute(e.getKey());
            result.mappings.put(e.getKey(), substitute);
        }
        return result;
    }
    
    
    
    /**
     * Gets the substitute type for the given type variable. If this set contains no
     * substitution for the passed variable, the variable itself is returned.
     * 
     * @param v The variable to find the substitute for.
     * @return The substitute type expression for the given variable or the variable 
     *          itself if there is no such substitute.
     */
    protected Type getSubstitute(TypeVar v) {
        final Type t = this.mappings.get(v);
        return t == null ? v : t;
    }
}
