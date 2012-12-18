package de.skuzzle.polly.parsing.ast.declarations.types;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.tools.Equatable;


public class MapTypeConstructor extends Type {
    
    private static final long serialVersionUID = 1L;



    private final static Identifier typeName(Collection<Type> source, Type target) {
        final StringBuilder b = new StringBuilder();
        b.append("(");
        for (final Type stype : source) {
            b.append(stype.getName());
            b.append(", ");
        }
        b.append("-> ");
        b.append(target.getName());
        b.append(")");
        
        return new Identifier(b.toString());
    }

    private final List<Type> source;
    private final Type target;
    
    
    
    public MapTypeConstructor(List<Type> source, Type target) {
        super(typeName(source, target), false, false);
        for (final Type t : source) {
            t.parent = this;
        }
        target.parent = this;
        
        this.source = Collections.unmodifiableList(source);
        this.target = target;
    }
    
    
    
    @Override
    protected void substituteTypeVar(TypeVar var, Type type) 
            throws ASTTraversalException {
        for (final Type stype : this.source) {
            stype.substituteTypeVar(var, type);
        }
        this.target.substituteTypeVar(var, type);
    }
    
    
    
    @Override
    public boolean isUnifiableWith(Type other) throws ASTTraversalException {
        if (other instanceof MapTypeConstructor) {
            final MapTypeConstructor mc = (MapTypeConstructor) other;
            if (this.source.size() != mc.source.size()) {
                return false;
            }
            final Iterator<Type> thisIt = this.source.iterator();
            final Iterator<Type> otherIt = mc.source.iterator();
            while (thisIt.hasNext()) {
                if (thisIt.next().isUnifiableWith(otherIt.next())) {
                    return false;
                }
            }
            return this.target.isUnifiableWith(mc.target);
        }
        return false;
    }
    
    
    
    public final List<Type> getSource() {
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

}
