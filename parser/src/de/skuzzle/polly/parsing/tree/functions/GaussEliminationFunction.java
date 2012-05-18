package de.skuzzle.polly.parsing.tree.functions;

import de.skuzzle.polly.parsing.ListType;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;


public class GaussEliminationFunction extends FunctionDeclaration {

    private static final long serialVersionUID = 1L;

    public GaussEliminationFunction() {
        super(new IdentifierLiteral("gaussian"), true);
        this.getFormalParameters().add(
            new VarDeclaration(
                new IdentifierLiteral("matrix"), 
                new ListType(new ListType(Type.NUMBER))));
        
        this.setExpression(new Functions.GaussianElimination());
    }

}
