package de.skuzzle.polly.parsing.ast.declarations.types;

import de.skuzzle.polly.parsing.ast.Identifier;


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

    private final ProductTypeConstructor source;
    private final Type target;
    
    
    
    public MapTypeConstructor(ProductTypeConstructor source, Type target) {
        super(typeName(source, target), false, false);
        source.parent = this;
        target.parent = this;
        
        this.source = source;
        this.target = target;
    }
    
    
    
    public final ProductTypeConstructor getSource() {
        return this.source;
    }
    
    
    
    public Type getTarget() {
        return this.target;
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
