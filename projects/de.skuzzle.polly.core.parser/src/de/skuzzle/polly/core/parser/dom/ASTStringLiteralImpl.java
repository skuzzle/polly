package de.skuzzle.polly.core.parser.dom;

import de.skuzzle.parser.dom.ASTNode;
import de.skuzzle.polly.dom.ASTStringLiteral;
import de.skuzzle.polly.dom.ASTVisitor;

public class ASTStringLiteralImpl extends AbstractASTExpression implements
        ASTStringLiteral {

    private String value;



    ASTStringLiteralImpl(String value) {
        if (value == null) {
            throw new NullPointerException("value"); //$NON-NLS-1$
        }
        this.value = value;
    }



    @Override
    public void resolveType() {
    }



    @Override
    public void updateRelationships(boolean deep) {
        // nothing to do
    }



    @Override
    public void replaceChild(ASTNode<ASTVisitor> child, ASTNode<ASTVisitor> newChild) {
        throw new UnsupportedOperationException("literal has no child"); //$NON-NLS-1$
    }



    @Override
    public boolean accept(ASTVisitor visitor) {
        if (visitor.shouldVisitStringLiterals) {
            switch (visitor.visit(this)) {
            case PROCESS_SKIP: return true;
            case PROCESS_ABORT: return false;
            }
            
            return visitor.leave(this) != PROCESS_ABORT;
        }
        return true;
    }



    @Override
    public String getValue() {
        return this.value;
    }



    @Override
    public void setValue(String value) {
        if (value == null) {
            throw new NullPointerException("value"); //$NON-NLS-1$
        }
        this.assertNotFrozen();
        this.value = value;
    }



    @Override
    public ASTStringLiteral getOrigin() {
        return super.getOrigin().as(ASTStringLiteral.class);
    }



    @Override
    public ASTStringLiteral deepOrigin() {
        return super.deepOrigin().as(ASTStringLiteral.class);
    }



    @Override
    public ASTStringLiteralImpl copy() {
        final ASTStringLiteralImpl copy = getNodeFactory().newStringLiteral(this.value);
        copy.setLocation(this.getLocation());
        copy.setOrigin(this);
        return copy;
    }
}