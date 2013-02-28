package de.skuzzle.polly.parsing.ast.declarations.types;

/**
 * Visitor interface which can be used to traverse {@link Type type expressions}.
 * 
 * @author Simon Taddiken
 */
public interface TypeVisitor {

    public void before(Type type);
    public void after(Type type);
    public void visit(Type type);
    
    public void before(ProductType p);
    public void after(ProductType p);
    public void visit(ProductType p);
    
    public void before(ListType l);
    public void after(ListType l);
    public void visit(ListType l);
    
    public void before(MapType m);
    public void after(MapType m);
    public void visit(MapType m);
    
    public void before(TypeVar v);
    public void after(TypeVar v);
    public void visit(TypeVar v);
}