package de.skuzzle.polly.parsing.ast.declarations.types;

import de.skuzzle.polly.parsing.ast.Identifier;


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
    public String toString() {
        return this.substitute == null || this.substitute == this
            ? this.getName().toString() : this.substitute.toString();
    }
    
    
    
    @Override
    public void visit(TypeVisitor visitor) {
        visitor.visitVar(this);
    }
}
