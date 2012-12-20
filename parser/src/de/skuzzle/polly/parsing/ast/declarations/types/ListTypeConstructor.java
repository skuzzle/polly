package de.skuzzle.polly.parsing.ast.declarations.types;


import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.tools.Equatable;


public class ListTypeConstructor extends Type {
    
    private static final long serialVersionUID = 1L;
    
    private final Type subType;

    
    public ListTypeConstructor(Type subType) {
        super(new Identifier("List<" + subType.getName() +">"), true, false);
        subType.parent = this;
        this.subType = subType;
    }
    
    
    
    public Type getSubType() {
        return this.subType;
    }


    
    @Override
    public Class<?> getEquivalenceClass() {
        return ListTypeConstructor.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        final ListTypeConstructor other = (ListTypeConstructor) o;
        return this.subType.equals(other.subType);
    }
    
    
    
    @Override
    public String toString() {
        return "List<" + this.subType.toString() + ">";
    }
    
    
    
    @Override
    public void visit(TypeVisitor visitor) {
        visitor.visitList(this);
    }
}
