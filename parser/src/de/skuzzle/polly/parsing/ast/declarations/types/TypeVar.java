package de.skuzzle.polly.parsing.ast.declarations.types;

import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.tools.Equatable;


public class TypeVar extends Type {

    private static final long serialVersionUID = 1L;
    
    private Type substitute;
    
    
    public TypeVar(Identifier name) {
        super(name, false, false);
    }
    
    
    
    protected void setSubstitute(Type t) {
        this.substitute = t;
    }
    
    
    
    @Override
    public Class<?> getEquivalenceClass() {
        return Type.class;
    }
    
    
    
    @Override
    public boolean actualEquals(Equatable o) {
        if (o instanceof TypeVar) {
            TypeVar tv = (TypeVar) o;
            return this.getName().equals(tv.getName());
        }
        // vars that have not been substituted are equal to any type
        return this.substitute == null ? true : this.substitute.equals(o);
    }

    
    
    @Override
    public String toString() {
        return this.substitute == null 
            ? this.getName().toString() : this.substitute.toString();
    }
    
    
    
    @Override
    public void visit(TypeVisitor visitor) {
        visitor.visitVar(this);
    }
}
