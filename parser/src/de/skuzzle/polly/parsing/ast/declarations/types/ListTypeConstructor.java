package de.skuzzle.polly.parsing.ast.declarations.types;


import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
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
    protected void substituteTypeVar(TypeVar var, Type type) 
            throws ASTTraversalException {
        this.subType.substituteTypeVar(var, type);
    }
    
    
    
    @Override
    public boolean isUnifiableWith(Type other) throws ASTTraversalException {
        if (other instanceof ListTypeConstructor) {
            final ListTypeConstructor lc = (ListTypeConstructor) other;
            return this.subType.isUnifiableWith(lc.subType);
        }
        return false;
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
}
