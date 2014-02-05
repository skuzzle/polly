package de.skuzzle.polly.core.parser.dom;

import de.skuzzle.parser.dom.ASTNode;
import de.skuzzle.polly.dom.ASTCallExpression;
import de.skuzzle.polly.dom.ASTExpression;
import de.skuzzle.polly.dom.ASTProductExpression;
import de.skuzzle.polly.dom.ASTVisitor;

public class ASTCallExpressionImpl extends AbstractASTExpression implements
        ASTCallExpression {

    private ASTExpression lhs;
    private ASTProductExpression rhs;


    
    public ASTCallExpressionImpl(ASTExpression lhs, ASTProductExpression rhs) {
        if (lhs == null) {
            throw new NullPointerException("lhs"); //$NON-NLS-1$
        } else if (rhs == null) {
            throw new NullPointerException("rhs"); //$NON-NLS-1$
        }
        this.lhs = lhs;
        this.rhs = rhs;
        this.updateRelationships(false);
    }
    
    

    @Override
    public void resolveType() {
    }



    @Override
    public void updateRelationships(boolean deep) {
        this.lhs.setParent(this);
        this.lhs.setPropertyInParent(LHS_EXPRESSION);
        
        if (deep) {
            this.lhs.updateRelationships(deep);
        }
        
        this.rhs.setParent(this);
        this.rhs.setPropertyInParent(RHS_EXPRESSION);
        
        if (deep) {
            this.rhs.updateRelationships(deep);
        }
        
        this.renewChildren(this.lhs, this.rhs);
        
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
        } else if (child == this.lhs) {
            if (!newChild.is(ASTExpression.class)) {
                throw new IllegalArgumentException(ERROR_NOT_EXPECTED_TYPE);
            }
            this.lhs = newChild.as(ASTExpression.class);
        } else if (child == this.rhs) {
            if (!newChild.is(ASTProductExpression.class)) {
                throw new IllegalArgumentException(ERROR_NOT_EXPECTED_TYPE);
            }
            this.rhs = newChild.as(ASTProductExpression.class);
        } else {
            throw new IllegalArgumentException(ERROR_NOT_A_CHILD);
        }
        this.updateRelationships(false);
    }



    @Override
    public boolean accept(ASTVisitor visitor) {
        if (!visitor.shouldVisitCalls) {
            return true;
        }
        
        switch (visitor.visit(this)) {
        case PROCESS_SKIP:  return true;
        case PROCESS_ABORT: return false;
        default:
            break;
        }
        
        if (!this.lhs.accept(visitor)) {
            return false;
        }
        if (!this.rhs.accept(visitor)) {
            return false;
        }
        
        return visitor.leave(this) != PROCESS_ABORT;
    }



    @Override
    public ASTExpression getLhs() {
        return this.lhs;
    }



    @Override
    public void setLhs(ASTExpression lhs) {
        if (lhs == null) {
            throw new NullPointerException("lhs"); //$NON-NLS-1$
        } else if (lhs == this) {
            throw new IllegalArgumentException(ERROR_SELF_AS_CHILD);
        }
        this.lhs = lhs;
        this.updateRelationships(false);
    }



    @Override
    public ASTProductExpression getRhs() {
        return this.rhs;
    }



    @Override
    public void setRhs(ASTProductExpression rhs) {
        if (rhs == null) {
            throw new NullPointerException("rhs"); //$NON-NLS-1$
        } else if (rhs == this) {
            throw new IllegalArgumentException(ERROR_SELF_AS_CHILD);
        }
        this.rhs = rhs;
        this.updateRelationships(false);
    }



    @Override
    public ASTCallExpressionImpl copy() {
        final ASTExpression lhsCopy = this.lhs.copy();
        final ASTProductExpression rhsCopy = this.rhs.copy();
        final ASTCallExpressionImpl copy = new ASTCallExpressionImpl(lhsCopy, rhsCopy);
        copy.setLocation(this.getLocation());
        return copy;
    }
}
