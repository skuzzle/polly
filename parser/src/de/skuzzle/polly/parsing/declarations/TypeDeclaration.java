package de.skuzzle.polly.parsing.declarations;

import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Type;


public class TypeDeclaration extends Declaration {

    private static final long serialVersionUID = 1L;

    
    public TypeDeclaration(Type type) {
        super(type.getTypeName(), true, false);
        this.setType(type);
    }
    

    @Override
    public void contextCheck(Namespace c) throws ParseException {
        // nothing to do here
    }
    
    

    @Override
    public Object clone() {
        return new TypeDeclaration(this.getType());
    }

}
