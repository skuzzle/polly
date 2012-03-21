package de.skuzzle.polly.parsing.tree.functions;

import de.skuzzle.polly.parsing.ListType;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;


public class ListLengthFunction extends FunctionDeclaration {
    
    private static final long serialVersionUID = 1L;
    
    public ListLengthFunction() {
        super(new IdentifierLiteral("length"), true);
        this.getFormalParameters().add(new VarDeclaration(
            new IdentifierLiteral("list"), new ListType(Type.ANY)));
        this.setExpression(new Functions.ListLength());
    }

}
