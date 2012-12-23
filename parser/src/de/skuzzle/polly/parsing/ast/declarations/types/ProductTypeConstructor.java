package de.skuzzle.polly.parsing.ast.declarations.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import de.skuzzle.polly.parsing.ast.Identifier;


public class ProductTypeConstructor extends Type implements Iterable<Type> {

    private static final long serialVersionUID = 1L;


    private static Identifier typeName(Collection<Type> types) {
        final StringBuilder b = new StringBuilder();
        b.append("(");
        final Iterator<Type> typeIt = types.iterator();
        while (typeIt.hasNext()) {
            b.append(typeIt.next().toString());
            if (typeIt.hasNext()) {
                b.append(", ");
            }
        }
        b.append(")");
        return new Identifier(b.toString());
    }
    
    
    
    private final List<Type> types;
    
    
    public ProductTypeConstructor(List<Type> types) {
        super(typeName(types), true, false);
        for (final Type t : types) {
            t.parent = this;
        }
        this.types = types;
    }
    
    
    
    
    public ProductTypeConstructor(Type...types) {
        this(Arrays.asList(types));
    }
    
    
    
    public List<Type> getTypes() {
        return this.types;
    }
    
    
    
    @Override
    public Type fresh() {
        final ListIterator<Type> it = this.types.listIterator();
        while (it.hasNext()) {
            final Type t = it.next();
            it.set(t.fresh());
        }
        return this;
    }

    
    
    @Override
    public Type substitute(TypeVar var, Type t) {
        final ListIterator<Type> it = this.types.listIterator();
        while (it.hasNext()) {
            final Type next = it.next();
            it.set(next.substitute(var, t));
        }
        return this;
    }
    
    
    @Override
    public String toString() {
        return typeName(this.types).toString();
    }
    
    
    
    @Override
    public void visit(TypeVisitor visitor) {
        visitor.visitProduct(this);
    }



    @Override
    public Iterator<Type> iterator() {
        return this.types.iterator();
    }
}
