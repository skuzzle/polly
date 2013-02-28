package de.skuzzle.polly.parsing.ast.declarations.types;

/**
 * Visitor interface which can be used to traverse {@link Type type expressions}.
 * 
 * @author Simon Taddiken
 */
public interface TypeVisitor {
    public final static int ABORT = -1;
    
    public final static int CONTINUE = 0;
    
    public final static int SKIP = 1;

    public int before(Type type);
    public int after(Type type);
    public boolean visit(Type type);
    
    public int before(ProductType p);
    public int after(ProductType p);
    public boolean visit(ProductType p);
    
    public int before(ListType l);
    public int after(ListType l);
    public boolean visit(ListType l);
    
    public int before(MapType m);
    public int after(MapType m);
    public boolean visit(MapType m);
    
    public int before(TypeVar v);
    public int after(TypeVar v);
    public boolean visit(TypeVar v);
}