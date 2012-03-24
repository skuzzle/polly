package de.skuzzle.polly.parsing.tree.functions;


import de.skuzzle.polly.parsing.ListType;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;




public class AggregateListFunction extends FunctionDeclaration {
    
    private static final long serialVersionUID = 1L;
    
    public AggregateListFunction(Functions.ListType type) {
        super(new IdentifierLiteral(type.name().toLowerCase()), true);
        this.getFormalParameters().add(new VarDeclaration(
            new IdentifierLiteral("list"), new ListType(Type.NUMBER)));
        this.setExpression(new Functions.List(type));
    }
}
