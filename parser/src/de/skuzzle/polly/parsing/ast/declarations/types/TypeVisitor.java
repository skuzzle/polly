package de.skuzzle.polly.parsing.ast.declarations.types;

/**
 * Visitor interface which can be used to traverse {@link Type type expressions}.
 * 
 * @author Simon Taddiken
 */
public interface TypeVisitor {

    public void beforePrimitive(Type type);
    public void afterPrimitive(Type type);
    public void visitPrimitive(Type type);
    
    public void beforeProduct(ProductType p);
    public void afterProduct(ProductType p);
    public void visitProduct(ProductType p);
    
    public void beforeList(ListType l);
    public void afterList(ListType l);
    public void visitList(ListType l);
    
    public void beforeMap(MapType m);
    public void afterMap(MapType m);
    public void visitMap(MapType m);
    
    public void beforeVar(TypeVar v);
    public void afterVar(TypeVar v);
    public void visitVar(TypeVar v);
}