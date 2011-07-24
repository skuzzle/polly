package de.skuzzle.polly.parsing.tree;

import java.util.Stack;

import de.skuzzle.polly.parsing.Context;
import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;


public class ForbiddenFunction extends FunctionDefinition {

    private static final long serialVersionUID = 1L;
    
    public ForbiddenFunction(IdentifierLiteral name) {
        super(name);
    }
    
    
    
    @Override
    public Expression contextCheck(Context context)
            throws ParseException {
        /* At this time, this has been resolved as a recursive function
         * definition which is invalid. So the only thing to do is to throw 
         * an exception. 
         * @see AssignmentExpression
         */
        
        throw new ParseException("Ungültige rekursive Funktionsdeklaration", 
                this.getPosition());
    }

    
    
    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        // do nothing;
    }

    
    
    @Override
    public Object clone() {
        ForbiddenFunction result = new ForbiddenFunction(this.getName());
        result.setPosition(this.getPosition());
        result.setType(this.getType());
        return result;
    }

}
