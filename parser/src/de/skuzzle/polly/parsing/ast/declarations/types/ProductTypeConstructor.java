package de.skuzzle.polly.parsing.ast.declarations.types;

import java.util.Collection;
import java.util.Iterator;

import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.tools.Equatable;


public class ProductTypeConstructor extends Type {

    private static final long serialVersionUID = 1L;


    private static Identifier typeName(Collection<Type> types) {
        final StringBuilder b = new StringBuilder();
        b.append("(");
        final Iterator<Type> typeIt = types.iterator();
        while (typeIt.hasNext()) {
            b.append(typeIt.next().getName());
            if (typeIt.hasNext()) {
                b.append(", ");
            }
        }
        b.append(")");
        return new Identifier(b.toString());
    }
    
    
    
    private final Collection<Type> types;
    
    
    public ProductTypeConstructor(Collection<Type> types) {
        super(typeName(types), true, false);
        this.types = types;
    }
    
    
    
    public Collection<Type> getTypes() {
        return this.types;
    }
    
    
    
    @Override
    protected void substituteTypeVar(TypeVar var, Type type)
            throws ASTTraversalException {
        for (final Type t : this.types) {
            t.substituteTypeVar(var, type);
        }
    }
    
    
    
    @Override
    public boolean isUnifiableWith(Type other) throws ASTTraversalException {
        if (other instanceof ProductTypeConstructor) {
            final ProductTypeConstructor pc = (ProductTypeConstructor) other;
            if (this.types.size() != pc.types.size()) {
                return false;
            }
            final Iterator<Type> thisIt = this.types.iterator();
            final Iterator<Type> otherIt = pc.types.iterator();
            while (thisIt.hasNext()) {
                if (thisIt.next().isUnifiableWith(otherIt.next())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    
    
    @Override
    public Class<?> getEquivalenceClass() {
        return ProductTypeConstructor.class;
    }
    
    
    
    @Override
    public boolean actualEquals(Equatable o) {
        final ProductTypeConstructor other = (ProductTypeConstructor) o;
        return this.types.equals(other.types);
    }

}
