package de.skuzzle.polly.parsing;

import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;


public class ListType extends Type {
    
    private static final long serialVersionUID = 1L;

    public final static ListType ANY_LIST = new ListType(Type.ANY);

    private Type subType;
    
    
    

    public ListType(Type primitive) {
        super(new IdentifierLiteral("List(of " + primitive.toString() + ")"), false);
        this.subType = primitive;
    }
    
    
    
    public Type getSubType() {
        return this.subType;
    }
    
    
    
    @Override
    public boolean check(Type other) {
        if (other == this) {
            return true;
        } else if (!(other instanceof ListType)) {
            return false;
        } else if (this.getSubType() == Type.ANY) {
            return true;
        } else {
            ListType o = (ListType) other;
            if (o.getSubType() == Type.ANY) {
                return true;
            }
            return ((ListType) other).getSubType().check(this.getSubType());
        }
    }
}
