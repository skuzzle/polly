package de.skuzzle.polly.parsing.ast.declarations.types;

import java.io.PrintStream;

import de.skuzzle.polly.parsing.util.DotBuilder;


public class TypeGraphVisualizer extends DefaultTypeVisitor {

    private DotBuilder dotBuilder;
    
    
    
    public void visualize(Type root, PrintStream out) {
        this.dotBuilder = new DotBuilder(out, true);
        root.visit(this);
        this.dotBuilder.finish();
    }
    
    
    
    @Override
    public void beforeList(ListTypeConstructor l) {
        this.dotBuilder.printNode(l, "List");
    }
    
    
    
    @Override
    public void beforePrimitive(Type type) {
        this.dotBuilder.printNode(type, type.getName().getId());
    }
    
    
    
    @Override
    public void beforeMap(MapTypeConstructor m) {
        this.dotBuilder.printNode(m, "Map");
    }
    
    
    
    @Override
    public void beforeProduct(ProductTypeConstructor p) {
        this.dotBuilder.printNode(p, "Product");
    }
    
    
    
    @Override
    public void beforeVar(TypeVar v) {
        this.dotBuilder.printNode(v, v.getName().getId());
    }
    
    
    
    @Override
    public void beforeSubstitute(Type s) {
        this.dotBuilder.printNode(s, s.getName().getId());
    }
    
    
    
    @Override
    public void afterList(ListTypeConstructor l) {
        this.dotBuilder.pop(l);
    }
    
    
    
    @Override
    public void afterPrimitive(Type type) {
        this.dotBuilder.pop(type);
    }
    
    
    
    @Override
    public void afterMap(MapTypeConstructor m) {
        this.dotBuilder.pop(m);
    }
    
    
    
    @Override
    public void afterProduct(ProductTypeConstructor p) {
        this.dotBuilder.pop(p);
    }
    
    
    
    @Override
    public void afterVar(TypeVar v) {
        this.dotBuilder.pop(v);
    }
    
    
    
    @Override
    public void afterSubstitute(Type s) {
        this.dotBuilder.pop(s);
    }
}
