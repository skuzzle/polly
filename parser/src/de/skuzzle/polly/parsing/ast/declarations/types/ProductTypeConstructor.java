package de.skuzzle.polly.parsing.ast.declarations.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import de.skuzzle.polly.parsing.ast.Identifier;


public class ProductTypeConstructor extends Type {

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
    
    
    
    private final Collection<Type> types;
    
    
    public ProductTypeConstructor(Collection<Type> types) {
        super(typeName(types), true, false);
        for (final Type t : types) {
            t.parent = this;
        }
        this.types = types;
    }
    
    
    
    
    public ProductTypeConstructor(Type...types) {
        this(Arrays.asList(types));
    }
    
    
    
    public Collection<Type> getTypes() {
        return this.types;
    }

    
    
    @Override
    public String toString() {
        return typeName(this.types).toString();
    }
    
    
    
    @Override
    public void visit(TypeVisitor visitor) {
        visitor.visitProduct(this);
    }
}
