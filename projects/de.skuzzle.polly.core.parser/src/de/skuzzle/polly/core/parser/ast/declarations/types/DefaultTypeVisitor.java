package de.skuzzle.polly.core.parser.ast.declarations.types;

import java.util.HashSet;
import java.util.Set;

/**
 * Default {@link TypeVisitor} implementation.
 * 
 * @author Simon Taddiken
 */
public class DefaultTypeVisitor implements TypeVisitor {

    private final Set<Type> visited;
    
    
    public DefaultTypeVisitor() {
        this.visited = new HashSet<Type>();
    }
    
    
    
    @Override
    public int before(Type type) {
        return CONTINUE;
    }

    @Override
    public int after(Type type) {
        return CONTINUE;
    }

    @Override
    public boolean visit(Type type) {
        switch (this.before(type)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        int after = this.after(type);
        this.visited.add(type);
        return after == CONTINUE;
    }
    
    

    @Override
    public int before(ProductType p) {
        return CONTINUE;
    }

    @Override
    public int after(ProductType p) {
        return CONTINUE;
    }

    @Override
    public boolean visit(ProductType p) {
        switch (this.before(p)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        for (final Type t : p.getTypes()) {
            if (!t.visit(this)) {
                return false;
            }
        }
        int after = this.after(p);
        this.visited.add(p);
        return after == CONTINUE;
    }

    
    
    @Override
    public int before(ListType l) {
        return CONTINUE;
    }

    @Override
    public int after(ListType l) {
        return CONTINUE;
    }

    @Override
    public boolean visit(ListType l) {
        switch (this.before(l)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        if (!l.getSubType().visit(this)) {
            return false;
        }
        int after = this.after(l);
        this.visited.add(l);
        return after == CONTINUE;
    }

    
    
    @Override
    public int before(MapType m) {
        return CONTINUE;
    }

    @Override
    public int after(MapType m) {
        return CONTINUE;
    }

    @Override
    public boolean visit(MapType m) {
        switch (this.before(m)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        if (!m.getSource().visit(this)) {
            return false;
        }
        
        if (!m.getTarget().visit(this)) {
            return false;
        }
        
        int after = this.after(m);
        this.visited.add(m);
        return after == CONTINUE;
    }

    
    
    @Override
    public int before(TypeVar v) {
        return CONTINUE;
    }
    
    @Override
    public int after(TypeVar v) {
        return CONTINUE;
    }
    
    public boolean visit(TypeVar v) {
        switch (this.before(v)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        int after = this.after(v);
        this.visited.add(v);
        return after == CONTINUE;
    }
}
