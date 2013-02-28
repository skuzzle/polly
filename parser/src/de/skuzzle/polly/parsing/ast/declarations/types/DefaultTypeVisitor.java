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
    public void before(Type type) {}

    @Override
    public void after(Type type) {}

    @Override
    public void visit(Type type) {
        if (this.aborted) {
            return;
        }
        this.before(type);
        this.after(type);
        this.visited.add(type);
    }
    
    

    @Override
    public void before(ProductType p) {}

    @Override
    public void after(ProductType p) {}

    @Override
    public void visit(ProductType p) {
        if (this.aborted) {
            return;
        }
        
        this.before(p);
        
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
        
        this.after(p);
        this.visited.add(p);
    }

    
    
    @Override
    public void before(ListType l) {}

    @Override
    public void after(ListType l) {}

    @Override
    public void visit(ListType l) {
        if (this.aborted) {
            return;
        }
        this.before(l);
        
        if (this.aborted) {
            return;
        }
        
        l.getSubType().visit(this);
        
        if (this.aborted) {
            return;
        }
        
        this.after(l);
        this.visited.add(l);
    }

    
    
    @Override
    public void before(MapType m) {}

    @Override
    public void after(MapType m) {}

    @Override
    public void visit(MapType m) {
        if (this.aborted) {
            return;
        }
        
        this.before(m);
        
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
        
        this.after(m);
        this.visited.add(m);
    }

    
    
    @Override
    public void before(TypeVar v) {}
    
    @Override
    public void after(TypeVar v) {}
    
    public void visit(TypeVar v) {
        if (this.aborted) {
            return;
        }
        
        this.before(v);
        
        if (this.aborted) {
            return;
        }
        
        this.after(v);
        this.visited.add(v);
    }
}
