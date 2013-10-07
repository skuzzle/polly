package de.skuzzle.polly.core.parser.ast.directives;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.TokenType;
import de.skuzzle.polly.core.parser.ast.Node;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversal;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ASTVisitor;
import de.skuzzle.polly.core.parser.ast.visitor.Transformation;


public class ReinterpretDirctive extends Directive {

    public ReinterpretDirctive(Position position) {
        super(position, TokenType.REINTERPRET);
    }

    @Override
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return true;
    }

    @Override
    public Node transform(Transformation transformation) throws ASTTraversalException {
        return this;
    }

    @Override
    public boolean traverse(ASTTraversal visitor) throws ASTTraversalException {
        return true;
    }

}
