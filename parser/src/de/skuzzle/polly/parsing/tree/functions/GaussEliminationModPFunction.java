package de.skuzzle.polly.parsing.tree.functions;

import de.skuzzle.polly.parsing.ListType;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;


public class GaussEliminationModPFunction extends FunctionDeclaration {

    private static final long serialVersionUID = 1L;

    public GaussEliminationModPFunction() {
        super(new IdentifierLiteral("gaussianModP"), true);
        this.getFormalParameters().add(
            new VarDeclaration(
                new IdentifierLiteral("matrix"), 
                new ListType(new ListType(Type.NUMBER))));
        this.getFormalParameters().add(
            new VarDeclaration(
                new IdentifierLiteral("p"), Type.NUMBER));
        this.setExpression(new Functions.GaussianEliminationModP());
    }

}
