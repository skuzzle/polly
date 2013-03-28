package de.skuzzle.polly.core.parser.ast.declarations.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.skuzzle.polly.core.parser.ast.Identifier;

/**
 * A product type represents the Cartesian product of a set of types.
 * 
 * @author Simon Taddiken
 */
public class ProductType extends Type implements Iterable<Type> {

    private static Identifier typeName(Collection<Type> types) {
        final StringBuilder b = new StringBuilder();
        //b.append("(");
        final Iterator<Type> typeIt = types.iterator();
        while (typeIt.hasNext()) {
            b.append(typeIt.next().getName().getId());
            if (typeIt.hasNext()) {
                b.append(" ");
            }
        }
        //b.append(")");
        return new Identifier(b.toString());
    }
    
    
    
    private final List<Type> types;
    
    
    
    /**
     * Creates a new product type with the given types.
     * 
     * @param types List of types in this product.
     */
    public ProductType(List<Type> types) {
        super(typeName(types), true, false);
        this.types = types;
    }
    
    
    
    /**
     * Creates a new product type from the given array.
     * 
     * @param types Array of types in this product.
     */
    public ProductType(Type...types) {
        this(Arrays.asList(types));
    }
    
    
    
    @Override
    public Type subst(Substitution s) {
        final List<Type> types = new ArrayList<Type>(this.types.size());
        for (final Type t : this.types) {
            types.add(t.subst(s));
        }
        return new ProductType(types);
    }
    
    
    
    /**
     * Gets the types in this product.
     * 
     * @return The types.
     */
    public List<Type> getTypes() {
        return this.types;
    }
    
    
    
    @Override
    public String toString() {
        return typeName(this.types).toString();
    }
    
    
    
    @Override
    public boolean visit(TypeVisitor visitor) {
        return visitor.visit(this);
    }



    @Override
    public Iterator<Type> iterator() {
        return this.types.iterator();
    }
    
    
    
    @Override
    public int compareTo(Type o) {
        if (!(o instanceof ProductType)) {
            throw new IllegalArgumentException("types can not be compared");
        }
        final ProductType other = (ProductType) o;
        assert this.types.size() == other.types.size();
        final Iterator<Type> thisIt = this.iterator();
        final Iterator<Type> otherIt = other.iterator();
        int thisI = 0;
        int otherI = 0;
        while (thisIt.hasNext()) {
            final Type thisNext = thisIt.next();
            final Type otherNext = otherIt.next();
            final int c = thisNext.compareTo(otherNext);
            if (c < 0) {
                ++otherI;
            } else if (c > 0) {
                ++thisI;
            }
        }
        return thisI - otherI;
    }
}
