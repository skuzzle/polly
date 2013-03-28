package de.skuzzle.polly.core.parser.ast.declarations.types;


import de.skuzzle.polly.core.parser.ast.Identifier;

/**
 * Represents the type of a list. Lists can have any other type as sub type.
 * 
 * @author Simon Taddiken
 */
public class ListType extends Type {
    
    private Type subType;

    
    /**
     * Creates a new list type with the given sub type.
     * 
     * @param subType Sub type of this list type.
     */
    ListType(Type subType) {
        super(new Identifier("list<" + subType.getName().getId() +">"), 
            subType.isComparable(), false);
        this.subType = subType;
    }
    
    
    
    @Override
    public Type subst(Substitution s) {
        return new ListType(this.subType.subst(s));
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
    public String toString() {
        return "list<" + this.subType.toString() + ">";
    }
    
    
    
    @Override
    public boolean visit(TypeVisitor visitor) {
        return visitor.visit(this);
    }
    
    
    
    @Override
    public int compareTo(Type o) {
        if (!(o instanceof ListType)) {
            throw new IllegalArgumentException("types can not be compared");
        }
        final ListType other = (ListType) o;
        return this.subType.compareTo(other.subType);
    }
}
