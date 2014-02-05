package de.skuzzle.polly.core.parser.dom;

import de.skuzzle.parser.dom.ASTNode;
import de.skuzzle.polly.dom.ASTBracedExpression;
import de.skuzzle.polly.dom.ASTExpression;
import de.skuzzle.polly.dom.ASTVisitor;

public class ASTBracedExpressionImpl extends AbstractASTExpression implements
        ASTBracedExpression {

    /** The expression in braces */
    private ASTExpression braced;



    public ASTBracedExpressionImpl(ASTExpression braced) {
        if (braced == null) {
            throw new NullPointerException("braced"); //$NON-NLS-1$
        }
        assertNotFrozen(braced);
        this.braced = braced;
        this.updateRelationships(false);
    }



    @Override
    public void resolveType() {
        this.braced.resolveType();
    }



    @Override
    public void updateRelationships(boolean deep) {
        this.braced.setParent(this);
        this.braced.setPropertyInParent(BRACED_EXPRESSION);
        this.renewChildren(this.braced);
        if (deep) {
            this.braced.updateRelationships(deep);
        }
    }



    @Override
    public void replaceChild(ASTNode<ASTVisitor> child, ASTNode<ASTVisitor> newChild) {
        this.assertNotFrozen();
        this.assertNotFrozen(newChild);
        
        if (newChild == child) {
            return;
        } else if (newChild == this) {
            throw new IllegalArgumentException(ERROR_SELF_AS_CHILD);
        } else if (newChild == null) {
            throw new NullPointerException("neWChild"); //$NON-NLS-1$
        } else if (this.getParent() != null && newChild == this.getParent()) {
            throw new IllegalArgumentException(ERROR_PARENT_AS_CHILD);
        } else if (!newChild.is(ASTExpression.class)) {
            throw new IllegalArgumentException(ERROR_NOT_EXPECTED_TYPE);
        } else if (child == this.braced) {
            this.braced = newChild.as(ASTExpression.class);
        } else {
            throw new IllegalArgumentException(ERROR_NOT_A_CHILD);
        }
        this.updateRelationships(false);
    }



    @Override
    public boolean accept(ASTVisitor visitor) {
        if (!visitor.shouldVisitBraced) {
            return true;
        }
        switch (visitor.visit(this)) {
        case PROCESS_SKIP: return true;
        case PROCESS_ABORT: return false;
        }
        
        if (this.braced != null && !this.braced.accept(visitor)) {
            return false;
        }
        
        return visitor.leave(this) != PROCESS_ABORT;
    }



    @Override
    public ASTExpression getBracedExpression() {
        return this.braced;
    }



    @Override
    public void setBracedExpression(ASTExpression expression) {
        if (expression == null) {
            throw new NullPointerException("expression"); //$NON-NLS-1$
        }
        this.assertNotFrozen();
        this.assertNotFrozen(expression);
        this.braced = expression;
        this.updateRelationships(false);
    }



    @Override
    public ASTBracedExpressionImpl copy() {
        ASTExpression bracedCopy = null; 
        bracedCopy = this.braced.copy();
        final ASTBracedExpressionImpl copy = new ASTBracedExpressionImpl(bracedCopy);
        copy.setLocation(this.getLocation());
        return copy;
    }

}
