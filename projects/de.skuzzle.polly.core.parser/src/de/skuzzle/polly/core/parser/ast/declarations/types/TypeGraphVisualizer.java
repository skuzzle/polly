package de.skuzzle.polly.core.parser.ast.declarations.types;

import java.io.PrintStream;

import de.skuzzle.polly.core.parser.util.PreOrderDotBuilder;

/**
 * Class to create a simple dot output from any type expression.
 * 
 * @author Simon Taddiken
 */
public class TypeGraphVisualizer extends DefaultTypeVisitor {

    private PreOrderDotBuilder dotBuilder;
    
    
    
    public void visualize(Type root, PrintStream out) {
        this.dotBuilder = new PreOrderDotBuilder(out, true);
        root.visit(this);
        this.dotBuilder.finish();
    }
    
    
    
    @Override
    public int before(ListType l) {
        this.dotBuilder.printNode(l, "List");
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Type type) {
        this.dotBuilder.printNode(type, type.getName().getId());
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(MapType m) {
        this.dotBuilder.printNode(m, "Map");
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(ProductType p) {
        this.dotBuilder.printNode(p, "Product");
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(TypeVar v) {
        this.dotBuilder.printNode(v, v.getName().getId());
        return CONTINUE;
    }
    
    
    
    @Override
    public int after(ListType l) {
        this.dotBuilder.pop(l);
        return CONTINUE;
    }
    
    
    
    @Override
    public int after(Type type) {
        this.dotBuilder.pop(type);
        return CONTINUE;
    }
    
    
    
    @Override
    public int after(MapType m) {
        this.dotBuilder.pop(m);
        return CONTINUE;
    }
    
    
    
    @Override
    public int after(ProductType p) {
        this.dotBuilder.pop(p);
        return CONTINUE;
    }
    
    
    
    @Override
    public int after(TypeVar v) {
        this.dotBuilder.pop(v);
        return CONTINUE;
    }
}
