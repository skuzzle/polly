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
                new IdentifierLiteral("lower"), Type.NUMBER));
        this.getFormalParameters().add(new VarDeclaration(
            new IdentifierLiteral("upper"), Type.NUMBER));
        
        this.setExpression(new Functions.Random());
    }
}
