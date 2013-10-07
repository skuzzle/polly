package de.skuzzle.polly.core.parser.ast.directives;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.TokenType;
import de.skuzzle.polly.core.parser.ast.Node;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversal;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ASTVisitor;
import de.skuzzle.polly.core.parser.ast.visitor.Transformation;


public class ProblemDirective extends Directive {

    public ProblemDirective(Position position) {
        super(position, TokenType.DIRECTIVE);
    }

    
    
    @Override
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return visitor.visit(this);
    }



    @Override
    public Node transform(Transformation transformation) throws ASTTraversalException {
        return transformation.transform(this);
    }



    @Override
    public boolean traverse(ASTTraversal visitor) throws ASTTraversalException {
        switch (visitor.before(this)) {
        case ASTTraversal.SKIP: return true;
        case ASTTraversal.ABORT: return false;
        }
        return visitor.after(this) == ASTTraversal.CONTINUE;
    }
}
