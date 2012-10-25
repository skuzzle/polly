package de.skuzzle.polly.parsing.tree.functions;

import de.skuzzle.polly.parsing.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;
import de.skuzzle.polly.parsing.types.Type;


public class MinMaxFunction extends FunctionDeclaration {

    private static final long serialVersionUID = 1L;

    
    
    public MinMaxFunction(Functions.MinMaxType type) {
        super(new IdentifierLiteral(type.toString().toLowerCase()), true);
        this.getFormalParameters().add(new VarDeclaration(new IdentifierLiteral("x"), 
                Type.NUMBER));
        this.getFormalParameters().add(new VarDeclaration(new IdentifierLiteral("y"), 
            Type.NUMBER));
        
        this.setExpression(new Functions.MinMax(type));
    }
}
