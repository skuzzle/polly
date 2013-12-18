package de.skuzzle.polly.core.parser.ast.declarations.types.unification;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.skuzzle.polly.core.parser.ast.declarations.types.ListType;
import de.skuzzle.polly.core.parser.ast.declarations.types.MapType;
import de.skuzzle.polly.core.parser.ast.declarations.types.MissingType;
import de.skuzzle.polly.core.parser.ast.declarations.types.ProductType;
import de.skuzzle.polly.core.parser.ast.declarations.types.Substitution;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.declarations.types.TypeVar;


public abstract class AbstractUnifier implements Unifier {
    
    protected final boolean subTypeIncl;
    
    
    public AbstractUnifier(boolean subTypeIncl) {
        this.subTypeIncl = subTypeIncl;
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

    /**
     * <p>Finds the representative of the equivalence class that <code>s</code> is in. If 
     * <code>s</code> was not yet assigned to a equivalence class, it is made the 
     * representative of a new class.</p>
     * 
     * @param s Type which' equivalence class's type will be resolved.
     * @return The representative type of the equivalence class that <code>s</code> is in.
     */
    protected abstract Type find(Type s);
    
    /**
     * Unions the two types into a single equivalence class. If the representative 
     * <code>x</code> of the equivalence class of <code>m</code> is no {@link TypeVar}, 
     * <code>x</code> is made the representative of the equivalence class that represents
     * the union of <code>m</code> and <code>n</code>. Otherwise, the representative 
     * <code>y</code> of the equivalence class of <code>n</code> is made the 
     * representative of the united equivalence class.
     * 
     * @param m Type to union.
     * @param n Type to union.
     */
    protected abstract void union(Type s, Type t, Map<TypeVar, Type> subst);
    
    
    protected boolean unifyInternal(Type m, Type n, Map<TypeVar, Type> subst) {
        final Type s = this.find(m);
        final Type t = this.find(n);
        
        if (this.subTypeIncl && s.isA(t) || s == t) {
            return true;
            
        } else if (s instanceof MissingType || t instanceof MissingType) {
            this.union(s, t, subst);
            return true;
            
        } else if (s instanceof MapType && t instanceof MapType) {
            this.union(s, t, subst);
            final MapType mc1 = (MapType) s;
            final MapType mc2 = (MapType) t;
            
            return this.unifyInternal(mc1.getSource(), mc2.getSource(), subst) &&
                this.unifyInternal(mc1.getTarget(), mc2.getTarget(), subst);
            
        } else if (s instanceof ListType && t instanceof ListType) {
            this.union(s, t, subst);
            final ListType l1 = (ListType) s;
            final ListType l2 = (ListType) t;
            
            return this.unifyInternal(l1.getSubType(), l2.getSubType(), subst);
            
        } else if (s instanceof ProductType && t instanceof ProductType) {
            this.union(s, t, subst);
            final ProductType p1 = (ProductType) s;
            final ProductType p2 = (ProductType) t;
            
            if (p1.getTypes().size() != p2.getTypes().size()) {
                return false;
            }
            final Iterator<Type> p2It = p2.getTypes().iterator();
            for (final Type p1Type : p1.getTypes()) {
                if (!this.unifyInternal(p1Type, p2It.next(), subst)) {
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
}
