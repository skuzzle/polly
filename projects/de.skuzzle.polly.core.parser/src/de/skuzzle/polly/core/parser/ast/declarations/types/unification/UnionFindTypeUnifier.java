package de.skuzzle.polly.core.parser.ast.declarations.types.unification;

import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.core.parser.ast.declarations.types.Substitution;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.declarations.types.TypeVar;


class UnionFindTypeUnifier extends AbstractUnifier {
    
    private final Map<Type, UnionFindItem<Type>> types;
    
    public UnionFindTypeUnifier(boolean subTypeIncl) {
        super(subTypeIncl);
        this.types = new HashMap<>();
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
    @Override
    public Substitution unify(Type first, Type second) {
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
    @Override
    public boolean tryUnify(Type first, Type second) {
        return this.unifyInternal(first, second, new HashMap<TypeVar, Type>());
    }
    
    
    
    @Override
    protected Type find(Type type) {
        UnionFindItem<Type> result = this.types.get(type);
        if (result == null) {
            result = new UnionFindItem<Type>(type);
        }
        result = result.root();
        this.types.put(type, result);
        return result.getValue();
    }

    
    
    @Override
    public void union(Type s, Type t, Map<TypeVar, Type> subst) {
        final UnionFindItem<Type> si = this.types.get(s).root();
        final UnionFindItem<Type> ti = this.types.get(t).root();

        final UnionFindItem<Type> rep = si.getValue() instanceof TypeVar ? ti : si;
        final UnionFindItem<Type> other = rep == si ? ti : si;
        
        other.mergeInto(rep);
        if (other.getValue() instanceof TypeVar) {
            subst.put((TypeVar) other.getValue(), rep.getValue());
        }
    }
}