package de.skuzzle.polly.parsing.ast.declarations.types;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Helper class to determine whether two types are structural equal and to resolve 
 * type variable substitution. One instance of this class can only be used to unify
 * one pair of types. So you need to create a new instance for each type pair to unify. 
 * 
 * @author Simon Taddiken
 */
final class TypeUnifier {

    private int classes;
    private final Map<Type, Integer> typeToClass;
    private final Map<Integer, Type> classToType;
    
    
    /**
     * Creates a new TypeUnifier which can then be used to test for equality of one pair
     * of types.
     */
    public TypeUnifier() {
        this.typeToClass = new HashMap<Type, Integer>();
        this.classToType = new HashMap<Integer, Type>();
    }
    
    
    
    /**
     * Tests for structural equality of the given type expressions. If 
     * <code>n</code> is an instance of <code>m</code>, all {@link TypeVar TypeVars} could
     * have been resolved. If so, all variables will be substituted with their resolved 
     * best matching substitute and the method returns <code>true</code>.
     *   
     * @param m Type to check.
     * @param n Type to check.
     * @param substitute Whether TypeVars should be substituted if unification was 
     *          successful.
     * @return Whether both type expressions are structural equal.
     */
    public boolean unify(Type m, Type n, boolean substitute) {
        boolean result = this.unifyInternal(m, n);
        if (result && substitute) {
            for (final Type t : this.typeToClass.keySet()) {
                if (t instanceof TypeVar) {
                    final TypeVar tv = (TypeVar) t;
                    Type representative = this.find(tv);
                    m.substitute(tv, representative);
                    n.substitute(tv, representative);
                }
            }
        }
        return result;
    }
    
    
    
    private boolean unifyInternal(Type m, Type n) {
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
     * Finds the representative of the equivalence class that <code>s</code> is in. If 
     * <code>s</code> was not yet assigned to a equivalence class, it is made the 
     * representative of a new class.
     * 
     * @param s Type which' equivalence class's type will be resolved.
     * @return The representative type of the equivalence class that <code>s</code> is in.
     */
    private Type find(Type s) {
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
        
        if (!(rep_m instanceof TypeVar)) {
            this.classToType.put(equiv, rep_m);
            this.typeToClass.put(rep_m, equiv);
            this.typeToClass.put(rep_n, equiv);
        } else { //if (!(rep_n instanceof TypeVar)) {
            this.classToType.put(equiv, rep_n);
            this.typeToClass.put(rep_m, equiv);
            this.typeToClass.put(rep_n, equiv);
        }
    }
}
