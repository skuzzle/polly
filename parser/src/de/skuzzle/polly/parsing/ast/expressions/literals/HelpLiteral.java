package de.skuzzle.polly.parsing.ast.expressions.literals;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Transformation;


public class HelpLiteral extends Literal {

    private static final long serialVersionUID = 1L;



    public HelpLiteral(Position position) {
        super(position, Type.HELP);
    }

    
    
    @Override
    public String format(LiteralFormatter formatter) {
        return formatter.formatHelp(this);
    }

    
    
    @Override
    public HelpLiteral transform(Transformation transformation) throws ASTTraversalException {
        return transformation.transformHelp(this);
    }

}
