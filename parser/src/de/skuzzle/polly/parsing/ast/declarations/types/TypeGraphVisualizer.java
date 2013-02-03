package de.skuzzle.polly.parsing.ast.declarations.types;

import java.io.PrintStream;

import de.skuzzle.polly.parsing.util.PreOrderDotBuilder;

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
    public void beforeList(ListType l) {
        this.dotBuilder.printNode(l, "List");
    }
    
    
    
    @Override
    public void beforePrimitive(Type type) {
        this.dotBuilder.printNode(type, type.getName().getId());
    }
    
    
    
    @Override
    public void beforeMap(MapType m) {
        this.dotBuilder.printNode(m, "Map");
    }
    
    
    
    @Override
    public void beforeProduct(ProductType p) {
        this.dotBuilder.printNode(p, "Product");
    }
    
    
    
    @Override
    public void beforeVar(TypeVar v) {
        this.dotBuilder.printNode(v, v.getName().getId());
    }
    
    
    
    @Override
    public void afterList(ListType l) {
        this.dotBuilder.pop(l);
    }
    
    
    
    @Override
    public void afterPrimitive(Type type) {
        this.dotBuilder.pop(type);
    }
    
    
    
    @Override
    public void afterMap(MapType m) {
        this.dotBuilder.pop(m);
    }
    
    
    
    @Override
    public void afterProduct(ProductType p) {
        this.dotBuilder.pop(p);
    }
    
    
    
    @Override
    public void afterVar(TypeVar v) {
        this.dotBuilder.pop(v);
    }
}
