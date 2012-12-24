package de.skuzzle.polly.parsing.ast.declarations.types;

import java.util.HashSet;
import java.util.Set;

/**
 * Default {@link TypeVisitor} implementation. Traversal process can be aborted any time
 * using {@link #abort()}.
 * 
 * @author Simon Taddiken
 */
public class DefaultTypeVisitor implements TypeVisitor {

    protected boolean aborted;
    private final Set<Type> visited;
    
    
    public DefaultTypeVisitor() {
        this.visited = new HashSet<Type>();
    }
    
    
    
    /**
     * Aborts the current traversal process after the next <code>visitXXX</code> method
     * finishes. 
     */
    public void abort() {
        this.aborted = true;
    }
    
    
    
    @Override
    public void beforePrimitive(Type type) {}

    @Override
    public void afterPrimitive(Type type) {}

    @Override
    public void visitPrimitive(Type type) {
        if (this.aborted) {
            return;
        }
        this.beforePrimitive(type);
        this.afterPrimitive(type);
        this.visited.add(type);
    }
    
    

    @Override
    public void beforeProduct(ProductTypeConstructor p) {}

    @Override
    public void afterProduct(ProductTypeConstructor p) {}

    @Override
    public void visitProduct(ProductTypeConstructor p) {
        if (this.aborted) {
            return;
        }
        
        this.beforeProduct(p);
        
        if (this.aborted) {
            return;
        }
        
        for (final Type t : p.getTypes()) {
            t.visit(this);
            
            if (this.aborted) {
                return;
            }
            
        }
        
        if (this.aborted) {
            return;
        }
        
        this.afterProduct(p);
        this.visited.add(p);
    }

    
    
    @Override
    public void beforeList(ListTypeConstructor l) {}

    @Override
    public void afterList(ListTypeConstructor l) {}

    @Override
    public void visitList(ListTypeConstructor l) {
        if (this.aborted) {
            return;
        }
        this.beforeList(l);
        
        if (this.aborted) {
            return;
        }
        
        l.getSubType().visit(this);
        
        if (this.aborted) {
            return;
        }
        
        this.afterList(l);
        this.visited.add(l);
    }

    
    
    @Override
    public void beforeMap(MapTypeConstructor m) {}

    @Override
    public void afterMap(MapTypeConstructor m) {}

    @Override
    public void visitMap(MapTypeConstructor m) {
        if (this.aborted) {
            return;
        }
        
        this.beforeMap(m);
        
        if (this.aborted) {
            return;
        }
        
        m.getSource().visit(this);
        
        if (this.aborted) {
            return;
        }
        
        m.getTarget().visit(this);
        
        if (this.aborted) {
            return;
        }
        
        this.afterMap(m);
        this.visited.add(m);
    }

    
    
    @Override
    public void beforeVar(TypeVar v) {}
    
    @Override
    public void afterVar(TypeVar v) {}
    
    public void visitVar(TypeVar v) {
        if (this.aborted) {
            return;
        }
        
        this.beforeVar(v);
        
        if (this.aborted) {
            return;
        }
        
        this.afterVar(v);
        this.visited.add(v);
    }
}
