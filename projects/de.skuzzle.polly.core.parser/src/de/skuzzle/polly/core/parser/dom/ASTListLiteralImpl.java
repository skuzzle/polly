package de.skuzzle.polly.core.parser.dom;

import java.util.List;

import de.skuzzle.parser.dom.ASTNode;
import de.skuzzle.polly.dom.ASTListLiteral;
import de.skuzzle.polly.dom.ASTLiteral;
import de.skuzzle.polly.dom.ASTVisitor;


public class ASTListLiteralImpl extends AbstractASTExpression implements ASTListLiteral {

    @Override
    public void resolveType() {
    }

    @Override
    public void updateRelationships(boolean deep) {
    }

    @Override
    public void replaceChild(ASTNode<ASTVisitor> child, ASTNode<ASTVisitor> newChild) {
    }

    @Override
    public boolean accept(ASTVisitor visitor) {
        return false;
    }

    @Override
    public List<? extends ASTLiteral> getValue() {
        return null;
    }

    @Override
    public ASTListLiteral copy() {
        return null;
    }

}
