package de.skuzzle.polly.parsing.tree.functions;

import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;



public class RandomFunction extends FunctionDeclaration {

    private static final long serialVersionUID = 1L;
    
    
    public RandomFunction() {
        super(new IdentifierLiteral("random"), true);
        this.getFormalParameters().add(new VarDeclaration(
                new IdentifierLiteral(";_number1"), Type.NUMBER));
        this.getFormalParameters().add(new VarDeclaration(
            new IdentifierLiteral(";_number2"), Type.NUMBER));
        
        this.setExpression(new Functions.Random());
    }
}
