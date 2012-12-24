package de.skuzzle.polly.parsing.ast.declarations.types;

import de.skuzzle.polly.parsing.ast.Identifier;

/**
 * Represents the type of a function. That is, a mapping from a product of types to 
 * another type.
 * 
 * @author Simon Taddiken
 */
public class MapTypeConstructor extends Type {
    
    private static final long serialVersionUID = 1L;



    private final static Identifier typeName(ProductTypeConstructor source, Type target) {
        final StringBuilder b = new StringBuilder();
        b.append("(");
        b.append(source.toString());
        b.append(" -> ");
        b.append(target.toString());
        b.append(")");
        
        return new Identifier(b.toString());
    }

    
    private ProductTypeConstructor source;
    private Type target;
    
    
    
    /**
     * Creates a new mapping type.
     * 
     * @param source Sorce types.
     * @param target Target type.
     */
    public MapTypeConstructor(ProductTypeConstructor source, Type target) {
        super(typeName(source, target), false, false);
        this.source = source;
        this.target = target;
    }
    
    
    
    /**
     * Gets the source of this mapping type.
     * 
     * @return the source.
     */
    public final ProductTypeConstructor getSource() {
        return this.source;
    }
    
    
    
    /**
     * Gets the target of this mapping type.
     * 
     * @return The target.
     */
    public Type getTarget() {
        return this.target;
    }
    
    
    
    @Override
    public Type fresh() {
        // invariant: valid cast
        this.source = (ProductTypeConstructor) this.source.fresh();
        this.target = this.target.fresh();
        return this;
    }
    
    
    
    @Override
    public Type substitute(TypeVar var, Type t) {
        this.source = (ProductTypeConstructor) this.source.substitute(var, t);
        this.target = this.target.substitute(var, t);
        return this;
    }
    
    
    
    @Override
    public String toString() {
        return typeName(this.source, this.target).toString();
    }
    
    
    
    @Override
    public void visit(TypeVisitor visitor) {
        visitor.visitMap(this);
    }
}
