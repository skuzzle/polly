package de.skuzzle.polly.parsing.ast.declarations.types;


public interface TypeVisitor {

    public void beforePrimitive(Type type);
    public void afterPrimitive(Type type);
    public void visitPrimitive(Type type);
    
    public void beforeProduct(ProductTypeConstructor p);
    public void afterProduct(ProductTypeConstructor p);
    public void visitProduct(ProductTypeConstructor p);
    
    public void beforeList(ListTypeConstructor l);
    public void afterList(ListTypeConstructor l);
    public void visitList(ListTypeConstructor l);
    
    public void beforeMap(MapTypeConstructor m);
    public void afterMap(MapTypeConstructor m);
    public void visitMap(MapTypeConstructor m);
    
    public void beforeVar(TypeVar v);
    public void afterVar(TypeVar v);
    public void visitVar(TypeVar v);
    
    public void beforeSubstitute(Type s);
    public void afterSubstitute(Type s);
    public void visitSubstitute(Type s);
}