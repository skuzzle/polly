package de.skuzzle.polly.core.parser.ast.declarations.types;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Helper class to determine whether two types are structural equal and to resolve 
 * type variable substitution. 
 * 
 * @author Simon Taddiken
 */
public final class TypeUnifier {
    
    

    private int classes;
    private final Map<Type, Integer> typeToClass;
    private final Map<Integer, Type> classToType;
    
    
    
    /**
     * Creates a new TypeUnifier which can then be used to test for equality of type
     * expressions.
     */
    public TypeUnifier() {
        this.typeToClass = new HashMap<Type, Integer>();
        this.classToType = new HashMap<Integer, Type>();
    }
    
    
    
    /**
     * Re-initializes this unifier. Needs to be done before unifying another set
     * of type expressions.
     */
    private final void init() {
        this.typeToClass.clear();
        this.classToType.clear();
        this.classes = 0;
    }
    
    
    
    /**
     * Tests for structural equality of the given type expression in the context of this 
     * unifier instance.
     * 
     * @param first First type to check. 
     * @param second Second type to check.
     * @return A substitution for the type variables in first and second or 
     *          <code>null</code> if unification was not successful.
     */
    public Substitution unify(Type first, Type second) {
        this.init();
        final Map<TypeVar, Type> subst = new HashMap<TypeVar, Type>();
        if (!unifyInternal(first, second, subst)) {
            return null;
        }
        return new Substitution(subst);
    }
    
    
    
    /**
     * Tests for structural equality of the two given type expressions.
     * 
     * @param first First type.
     * @param second Second type.
     * @return Whether the first type is an instance of the second type
     */
    public boolean tryUnify(Type first, Type second) {
        this.init();
        return this.unifyInternal(first, second, new HashMap<TypeVar, Type>());
    }

    
    
    private final boolean unifyInternal(Type m, Type n, Map<TypeVar, Type> subst) {
        final Type s = this.find(m);
        final Type t = this.find(n);
        
        if (s.isA(t)) {
            return true;
        } else if (s instanceof MapType && t instanceof MapType) {
            this.union(s, t, subst);
            final MapType mc1 = (MapType) s;
            final MapType mc2 = (MapType) t;
            return unifyInternal(mc1.getSource(), mc2.getSource(), subst) && 
                unifyInternal(mc1.getTarget(), mc2.getTarget(), subst);
            
        } else if (s instanceof ListType && t instanceof ListType) {
            this.union(s, t, subst);
            final ListType lc1 = (ListType) s;
            final ListType lc2 = (ListType) t;
            return this.unifyInternal(lc1.getSubType(), lc2.getSubType(), subst);
            
        } else if (s instanceof ProductType && 
                    t instanceof ProductType) {
            
            // CONSIDER: unify single type Products with simple types?
            
            this.union(s, t, subst);
            final ProductType pc1 = (ProductType) s;
            final ProductType pc2 = (ProductType) t;
            
            if (pc1.getTypes().size() != pc2.getTypes().size()) {
                return false;
            }
            final Iterator<Type> pc2It = pc2.getTypes().iterator();
            for (final Type pc1T : pc1.getTypes()) {
                if (!this.unifyInternal(pc1T, pc2It.next(), subst)) {
                    return false;
                }
            }
            return true;
            
        } else if (s instanceof TypeVar || t instanceof TypeVar) {
            this.union(s, t, subst);
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
    private void union(Type m, Type n, Map<TypeVar, Type> subst) {
        final Type rep_m = this.find(m);
        final Type rep_n = this.find(n);
        
        final int equiv = this.getEquivClass(m);
        
        final Type representative = rep_m instanceof TypeVar ? rep_n : rep_m;
        final Type other = representative == rep_m ? rep_n : rep_m;
        this.makeEquivalent(equiv, representative, other);
        
        if (other instanceof TypeVar) {
            subst.put((TypeVar) other, representative);
        }
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
