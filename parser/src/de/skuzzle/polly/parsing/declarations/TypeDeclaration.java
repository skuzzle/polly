package de.skuzzle.polly.parsing.declarations;

import de.skuzzle.polly.parsing.types.Type;


public class TypeDeclaration extends Declaration {

    private static final long serialVersionUID = 1L;

    
    public TypeDeclaration(Type type) {
        super(type.getTypeName(), true, false);
        this.setType(type);
    }
    

    
    @Override
    public String toString() {
        return "(TYPE) " + this.getName();
    }
}
