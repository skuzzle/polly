package de.skuzzle.polly.parsing.tree;


import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;


public class ForbiddenFunction extends FunctionDeclaration {

    private static final long serialVersionUID = 1L;
    
    public ForbiddenFunction(IdentifierLiteral name) {
        super(name, false, false);
    }
    
    
    
    @Override
    public void contextCheck(Namespace context)
            throws ParseException {
        /* At this time, this has been resolved as a recursive function
         * definition which is invalid. So the only thing to do is to throw 
         * an exception. 
         * @see AssignmentExpression
         */
        
        throw new ParseException("Ungültige rekursive Funktionsdeklaration", 
                this.getName().getPosition());
    }

    
    @Override
    public Object clone() {
        ForbiddenFunction result = new ForbiddenFunction(this.getName());
        return result;
    }

}
