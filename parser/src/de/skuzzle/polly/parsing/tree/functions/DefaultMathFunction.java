package de.skuzzle.polly.parsing.tree.functions;

import de.skuzzle.polly.parsing.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;
import de.skuzzle.polly.parsing.types.Type;


public class DefaultMathFunction extends FunctionDeclaration {

    private static final long serialVersionUID = 1L;

    public DefaultMathFunction(Functions.MathType type) {
        super(new IdentifierLiteral(type.name().toLowerCase()), true);
        this.getFormalParameters().add(
            new VarDeclaration(new IdentifierLiteral("x"), Type.NUMBER));
        this.setExpression(new Functions.Math(type));
    }
}
