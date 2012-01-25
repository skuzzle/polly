package de.skuzzle.polly.parsing.declarations;

import de.skuzzle.polly.parsing.tree.Expression;
import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;


public class ConstantDeclaration extends VarDeclaration {
        
    private static final long serialVersionUID = 1L;

    public ConstantDeclaration(IdentifierLiteral name, Expression expression) {
        super(name, true, false);
        this.setExpression(expression);
    }
}
