package de.skuzzle.polly.core.parser.ast.declarations.types;

import de.skuzzle.polly.core.parser.ast.Identifier;

/**
 * Represents the type of a function. That is, a mapping from a product of types to 
 * another type.
 * 
 * @author Simon Taddiken
 */
public class MapType extends Type {
    
    private final static Identifier typeName(Type source, Type target) {
        final StringBuilder b = new StringBuilder();
        b.append("(");
        b.append(source.getName().getId());
        b.append(" -> ");
        b.append(target.getName().getId());
        b.append(")");
        
        return new Identifier(b.toString());
    }

    
    private Type source;
    private Type target;
    
    
    
    /**
     * Creates a new mapping type.
     * 
     * @param source Sorce types.
     * @param target Target type.
     */
    MapType(Type source, Type target) {
        super(typeName(source, target), false, false);
        this.source = source;
        this.target = target;
    }
    
    
    
    @Override
    public Type subst(Substitution s) {
        final Type source = this.source.subst(s);
        final Type target = this.target.subst(s);
        return new MapType(source, target);
    }
    
    
    
    /**
     * Gets the source of this mapping type.
     * 
     * @return the source.
     */
    public final Type getSource() {
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
    public String toString() {
        return typeName(this.source, this.target).toString();
    }
    
    
    
    @Override
    public boolean visit(TypeVisitor visitor) {
        return visitor.visit(this);
    }
}
