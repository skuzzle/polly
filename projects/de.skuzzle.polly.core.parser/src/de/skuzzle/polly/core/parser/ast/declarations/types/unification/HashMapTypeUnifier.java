package de.skuzzle.polly.core.parser.ast.declarations.types.unification;


import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.core.parser.ast.declarations.types.Substitution;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.declarations.types.TypeVar;


/**
 * Helper class to determine whether two types are structural equal and to resolve 
 * type variable substitution. 
 * 
 * @author Simon Taddiken
 */
final class HashMapTypeUnifier extends AbstractUnifier {
    
    

    private int classes;
    private final Map<Type, Integer> typeToClass;
    private final Map<Integer, Type> classToType;
    
    
    
    /**
     * Creates a new TypeUnifier which can then be used to test for equality of type
     * expressions.
     * @param subTypeIncl Whether sub type inclusion should be checked when comparing
     *          primitives.
     */
    public HashMapTypeUnifier(boolean subTypeIncl) {
        super(subTypeIncl);
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
    
    
    
    @Override
    public Substitution unify(Type first, Type second) {
        this.init();
        return super.unify(first, second);
    }
    
    

    @Override
    public boolean tryUnify(Type first, Type second) {
        this.init();
        return super.tryUnify(first, second);
    }

    

    @Override
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
    
    

    @Override
    protected void union(Type m, Type n, Map<TypeVar, Type> subst) {
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
