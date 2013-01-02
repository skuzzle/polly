package de.skuzzle.polly.parsing.ast.declarations.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Helper class to determine whether two types are structural equal and to resolve 
 * type variable substitution. One instance of this class can only be used to unify
 * one pair of types. So you need to create a new instance for each type pair to unify. 
 * 
 * @author Simon Taddiken
 */
public final class TypeUnifier {

    private int classes;
    private final Map<Type, Integer> typeToClass;
    private final Map<Integer, Type> classToType;
    private final Map<Type, Type> substitutions;
    
    
    
    /**
     * Creates a new TypeUnifier which can then be used to test for equality of one pair
     * of types.
     */
    public TypeUnifier() {
        this.typeToClass = new HashMap<Type, Integer>();
        this.classToType = new HashMap<Integer, Type>();
        this.substitutions = new HashMap<Type, Type>();
    }
    
    
    
    public void removeMapping(Type type) {
        this.substitutions.remove(type);
    }
    
    
    
    /**
     * tests for structural equality of the given type expression in the context of this 
     * unifier instance. On success, all found substitutions for variables will be 
     * remembered.
     * 
     * @param first First type to check. 
     * @param second Second type to check.
     * @return Whether both type expressions are structurally equal.
     */
    public boolean unify(Type first, Type second) {
        return this.canUnify(first, second, true);
    }
    
    
    
    /**
     * Tests for structural equality of the given type expressions in the context of this
     * unifier instance.
     *  
     * @param first First type to check. 
     * @param second Second type to check.
     * @param rememberSubstitutions Whether substitutions for type variables that are 
     *          found during unification should be remembered.
     * 
     * @return Whether both type expressions are structurally equal.
     */
    public boolean canUnify(Type first, Type second, boolean rememberSubstitutions) {
        this.classToType.clear();
        this.typeToClass.clear();
        for (final Entry<Type, Type> e : this.substitutions.entrySet()) {
            this.union(e.getKey(), e.getValue());
        }
        boolean result = this.unifyInternal(first, second);
        if (result && rememberSubstitutions) {
            for (final Type t : this.typeToClass.keySet()) {
                if (t instanceof TypeVar) {
                    this.substitutions.put(t, this.find(t));
                }
            }
        }
        return result;
    }
    
    
    
    /**
     * Substitutes all known type variables in the given type expression and returns
     * a new type expression.
     * 
     * @param t Root node for the substitution process.
     * @return New type expression where all known variables were substituted. 
     */
    public Type substitute(Type t) {
        if (t.isPrimitve()) {
            return t;
            
        } else if (t instanceof TypeVar) {
            Type t1 = this.substitutions.get(t);
            return t1 == null ? t : t1;
            
        } else if (t instanceof MapTypeConstructor) {
            final MapTypeConstructor mtc = (MapTypeConstructor) t;
            return new MapTypeConstructor(
                (ProductTypeConstructor) this.substitute(mtc.getSource()), 
                this.substitute(mtc.getTarget()));
            
        } else if (t instanceof ListTypeConstructor) {
            final ListTypeConstructor lt = (ListTypeConstructor) t;
            return new ListTypeConstructor(this.substitute(lt.getSubType()));
            
        } else if (t instanceof ProductTypeConstructor) {
            final ProductTypeConstructor ptc = (ProductTypeConstructor) t;
            final List<Type> substituted = new ArrayList<Type>(ptc.getTypes().size());
            for (final Type type : ptc.getTypes()) {
                substituted.add(this.substitute(type));
            }
            return new ProductTypeConstructor(substituted);
        }
        throw new IllegalStateException("can not happen");
    }
    
    
    
    /**
     * Creates a new type expression which is structurally equal to the given one but 
     * replaces all occurring type variables with new ones. Same variables will be
     * replaced by the same new variable.
     * 
     * @param t Root of the type expression to "refresh".
     * @return New refreshed type expression.
     */
    public Type fresh(Type t) {
        return this.freshInternal(t, new HashMap<Type, Type>());
    }
    
    
    
    /**
     * Refreshes each type expression in the given collection with new type variables.
     * 
     * @param types Collection of type expressions to refresh.
     * @return New Collection of refreshed types.
     * @see #fresh(Type)
     */
    public List<Type> freshAll(Collection<Type> types) {
        final List<Type> result = new ArrayList<Type>(types.size());
        for (final Type t : types) {
            result.add(this.fresh(t));
        }
        return result;
    }
    
    
    
    private Type freshInternal(Type t, Map<Type, Type> map) {
        if (t.isPrimitve()) {
            return t;
            
        } else if (t instanceof TypeVar) {
            Type t1 = map.get(t);
            if (t1 == null) {
                t1 = Type.newTypeVar();
                map.put(t, t1);
            }
            return t1;
            
        } else if (t instanceof MapTypeConstructor) {
            final MapTypeConstructor mtc = (MapTypeConstructor) t;
            return new MapTypeConstructor(
                (ProductTypeConstructor) this.freshInternal(mtc.getSource(), map), 
                this.freshInternal(mtc.getTarget(), map));
            
        } else if (t instanceof ListTypeConstructor) {
            final ListTypeConstructor lt = (ListTypeConstructor) t;
            return new ListTypeConstructor(this.freshInternal(lt.getSubType(), map));
            
        } else if (t instanceof ProductTypeConstructor) {
            final ProductTypeConstructor ptc = (ProductTypeConstructor) t;
            final List<Type> substituted = new ArrayList<Type>(ptc.getTypes().size());
            for (final Type type : ptc.getTypes()) {
                substituted.add(this.freshInternal(type, map));
            }
            return new ProductTypeConstructor(substituted);
        }
        throw new IllegalStateException("can not happen");
    }
    
    
    
    private final boolean unifyInternal(Type m, Type n) {
        final Type s = this.find(m);
        final Type t = this.find(n);
        
        if (s == t) {
            return true;
        } else if (s.isPrimitve() && t.isPrimitve() && s == t) {
            return true;
        } else if (s instanceof MapTypeConstructor && t instanceof MapTypeConstructor) {
            this.union(s, t);
            final MapTypeConstructor mc1 = (MapTypeConstructor) s;
            final MapTypeConstructor mc2 = (MapTypeConstructor) t;
            return unifyInternal(mc1.getSource(), mc2.getSource()) && 
                unifyInternal(mc1.getTarget(), mc2.getTarget());
            
        } else if (s instanceof ListTypeConstructor && t instanceof ListTypeConstructor) {
            this.union(s, t);
            final ListTypeConstructor lc1 = (ListTypeConstructor) s;
            final ListTypeConstructor lc2 = (ListTypeConstructor) t;
            return this.unifyInternal(lc1.getSubType(), lc2.getSubType());
            
        } else if (s instanceof ProductTypeConstructor && 
                    t instanceof ProductTypeConstructor) {
            
            // CONSIDER: unify single type Products with simple types?
            
            this.union(s, t);
            final ProductTypeConstructor pc1 = (ProductTypeConstructor) s;
            final ProductTypeConstructor pc2 = (ProductTypeConstructor) t;
            
            if (pc1.getTypes().size() != pc2.getTypes().size()) {
                return false;
            }
            final Iterator<Type> pc2It = pc2.getTypes().iterator();
            for (final Type pc1T : pc1.getTypes()) {
                if (!this.unifyInternal(pc1T, pc2It.next())) {
                    return false;
                }
            }
            return true;
            
        } else if (s instanceof TypeVar || t instanceof TypeVar) {
            this.union(s, t);
            return true;
        } else {
            return false;
        }
    }
    
    
    
    /**
     * <p>Finds the representative of the equivalence class that <code>s</code> is in. If 
     * <code>s</code> was not yet assigned to a equivalence class, it is made the 
     * representative of a new class.</p>
     * 
     * <p>For two type expressions <code>m,n</code> that are unifiable as determined by 
     * {@link #canUnify(Type, Type, boolean)}, this method would also return the valid 
     * substitution for any type variable occurring in <code>m</code> and <code>n</code>. 
     * 
     * @param s Type which' equivalence class's type will be resolved.
     * @return The representative type of the equivalence class that <code>s</code> is in.
     */
    public Type find(Type s) {
        final int cls = this.getEquivClass(s);
        Type representant = this.classToType.get(cls);
        if (representant == null) {
            representant = s;
            this.classToType.put(cls, representant);
        }
        return representant;
    }
    
    
    
    /**
     * Gets the equivalence class of the given type. If 
     * <code>t</code> was not yet assigned to a equivalence class, it is made the 
     * representative of a new class.
     * 
     * @param t Type which equivalence class should be resolved.
     * @return The type's equivalence class.
     */
    private int getEquivClass(Type t) {
        Integer i = this.typeToClass.get(t);
        if (i == null) {
            i = this.classes++;
            this.typeToClass.put(t, i);
        }
        return i;
    }
    
    
    
    /**
     * Unions the two types into a single equivalence class. If the representative 
     * <code>x</code> of the equivalence class of <code>m</code> is no {@link TypeVar}, 
     * <code>x</code> is made the representative of the equivalence class that represents
     * the union of <code>m</code> and <code>n</code>. Otherwise, the representative 
     * <code>y</code> of the equivalence class of <code>n</code> is made the representant
     * of the united equivalence class.
     * 
     * @param m Type to union.
     * @param n Type to union.
     */
    private void union(Type m, Type n) {
        final Type rep_m = this.find(m);
        final Type rep_n = this.find(n);
        
        final int equiv = this.getEquivClass(m);
        
        final Type representative = rep_m instanceof TypeVar ? rep_n : rep_m;
        final Type other = representative == rep_m ? rep_n : rep_m;
        this.makeEquivalent(equiv, representative, other);
    }
    
    
    
    /**
     * Makes both given types equivalent, that is both gets assigned the given equivalence 
     * class and the first type is made the new representative of the given class.
     * 
     * @param equivClass Equivalence class to put both types in.
     * @param representative First type. Is also made the new representative of the 
     *          equivalence class.
     * @param other Second type to put in the equivalence class.
     */
    private final void makeEquivalent(int equivClass, Type representative, Type other) {
        // make type the new representative of the given class
        this.classToType.put(equivClass, representative);
        // assign new equivalence class to both types
        this.typeToClass.put(representative, equivClass);
        this.typeToClass.put(other, equivClass);
    }
}
