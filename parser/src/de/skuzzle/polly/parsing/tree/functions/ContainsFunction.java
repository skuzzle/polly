package de.skuzzle.polly.parsing.tree.functions;

import de.skuzzle.polly.parsing.ListType;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;


public class ContainsFunction extends FunctionDeclaration {

    private static final long serialVersionUID = 1L;

    public ContainsFunction() {
        super(new IdentifierLiteral("contains"), true);
        this.getFormalParameters().add(
            new VarDeclaration(new IdentifierLiteral("list"), new ListType(Type.ANY)));
        this.getFormalParameters().add(
            new VarDeclaration(new IdentifierLiteral("item"), Type.ANY));
        
        this.setExpression(new Functions.Contains());
    }
}
