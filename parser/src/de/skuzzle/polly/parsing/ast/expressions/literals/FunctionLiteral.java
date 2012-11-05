package de.skuzzle.polly.parsing.ast.expressions.literals;


import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.types.FunctionType;
import de.skuzzle.polly.parsing.types.Type;


public class FunctionLiteral extends Literal {

    private final FunctionDeclaration function;
    
    
    
    FunctionLiteral(Position position, FunctionDeclaration function) {
        super(position, (FunctionType) function.getType());
        this.function = function;
    }

    
    
    public FunctionDeclaration getFunction() {
        return this.function;
    }
    
    
    
    @Override
    public Literal castTo(Type type) throws ASTTraversalException {
        throw new ASTTraversalException("not castable");
    }
    
    

    @Override
    public String format(LiteralFormatter formatter) {
        return null;
    }
}