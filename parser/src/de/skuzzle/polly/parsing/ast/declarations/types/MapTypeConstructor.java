package de.skuzzle.polly.parsing.ast.declarations.types;

import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.tools.Equatable;


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
    
    
    
    @Override
    protected void substituteTypeVar(TypeVar var, Type type) 
            throws ASTTraversalException {
        this.source.substituteTypeVar(var, type);
        this.target.substituteTypeVar(var, type);
    }
    
    
    
    @Override
    protected boolean canSubstitute(TypeVar var, Type type) {
        return this.source.canSubstitute(var, type) && 
            this.target.canSubstitute(var, type);
    }
    
    
    
    @Override
    public boolean isUnifiableWith(Type other, boolean unify) throws ASTTraversalException {
        if (other instanceof MapTypeConstructor) {
            final MapTypeConstructor mc = (MapTypeConstructor) other;
            return this.source.isUnifiableWith(mc.source, unify) && 
                this.target.isUnifiableWith(mc.target, unify);
        }
        return false;
    }
    
    
    
    public final ProductTypeConstructor getSource() {
        return this.source;
    }
    
    
    
    public Type getTarget() {
        return this.target;
    }
    
    
    
    @Override
    public boolean actualEquals(Equatable o) {
        final MapTypeConstructor other = (MapTypeConstructor) o;
        return this.source.equals(other.source) && this.target.equals(other.target);
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
