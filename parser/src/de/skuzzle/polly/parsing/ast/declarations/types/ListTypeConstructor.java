package de.skuzzle.polly.parsing.ast.declarations.types;


import de.skuzzle.polly.parsing.ast.Identifier;

/**
 * Represents the type of a list. Lists can have any other type as sub type.
 * 
 * @author Simon Taddiken
 */
public class ListTypeConstructor extends Type {
    
    private static final long serialVersionUID = 1L;
    
    private Type subType;

    
    /**
     * Creates a new list type with the given sub type.
     * 
     * @param subType Sub type of this list type.
     */
    public ListTypeConstructor(Type subType) {
        super(new Identifier("List<" + subType.getName() +">"), true, false);
        this.subType = subType;
    }
    
    
    
    /**
     * Gets this type's sub type.
     * 
     * @return The subtype.
     */
    public Type getSubType() {
        return this.subType;
    }
    
    
    
    @Override
    public Type fresh() {
        this.subType = this.subType.fresh();
        return this;
    }
    
    
    
    @Override
    public Type substitute(TypeVar var, Type t) {
        this.subType = this.subType.substitute(var, t);
        return this;
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
