package de.skuzzle.polly.core.parser.ast.declarations.types;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class represents a mapping of type variables to other type expressions. 
 * @author Simon Taddiken
 */
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
                    ((TypeVar) te).source = v;
                    this.mappings.put(v, te);
                }
                return te;
            }
        };
    }
    

    
    /** Substitution mappings in this instance */
    protected final Map<TypeVar, Type> mappings;
    
    
    
    /**
     * Creates a new substitution from the given mapping.
     *  
     * @param mappings Mappinf of type variables to type expressions.
     */
    public Substitution(Map<TypeVar, Type> mappings) {
        this.mappings = mappings;
    }
    
    
    
    /**
     * Creates a new empty substutiton.
     */
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
    
    
    
    public Substitution toSource() {
        final Substitution result = new Substitution(
            new HashMap<TypeVar, Type>());
        for (final Entry<TypeVar, Type> e : this.mappings.entrySet()) {
            if (e.getKey().source != null) {
                result.mappings.put(e.getKey().source, e.getValue());
            }
        }
        return result;
    }
    
    
    public Substitution join(Substitution s) {
        final Substitution result = new Substitution(
            new HashMap<TypeVar, Type>());
        result.mappings.putAll(this.mappings);
        if (s != null) {
            result.mappings.putAll(s.mappings);
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
    
    
    
    /**
     * Gets a read-only map view of the substitution mappings.
     * 
     * @return Map of substitutions.
     */
    public Map<TypeVar, Type> map() {
        return Collections.unmodifiableMap(this.mappings);
    }
}
