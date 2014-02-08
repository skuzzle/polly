package de.skuzzle.polly.core.parser.dom;

import de.skuzzle.parser.dom.ASTNode;
import de.skuzzle.polly.dom.ASTBinaryExpression;
import de.skuzzle.polly.dom.ASTExpression;
import de.skuzzle.polly.dom.ASTOperator;
import de.skuzzle.polly.dom.ASTProductExpression;
import de.skuzzle.polly.dom.ASTVisitor;

public class ASTBinaryExpressionImpl extends AbstractASTExpression implements
        ASTBinaryExpression {

    /** The left operand of this expression */
    private ASTExpression left;
    /** The operator of this expression */
    private ASTOperator op;
    /** The right operand of this expressiom */
    private ASTExpression right;



    ASTBinaryExpressionImpl(ASTOperator op, ASTExpression left, ASTExpression right) {
        if (op == null) {
            throw new NullPointerException("op"); //$NON-NLS-1$
        } else if (left == null) {
            throw new NullPointerException("left"); //$NON-NLS-1$
        } else if (right == null) {
            throw new NullPointerException("right"); //$NON-NLS-1$
        }
        this.assertNotFrozen(op);
        this.assertNotFrozen(left);
        this.assertNotFrozen(right);
        this.left = left;
        this.op = op;
        this.right = right;
        this.updateRelationships(false);
    }



    @Override
    public void resolveType() {
    }

    
    
    @Override
    public ASTCallExpressionImpl asCall() {
        final ASTExpression lhs = getNodeFactory().newIdExpression(this.op.copy());
        final ASTProductExpression rhs = getNodeFactory().newProduct(
                this.left.copy(), this.right.copy());
        final ASTCallExpressionImpl call = getNodeFactory().newCall(lhs, rhs);
        call.setLocation(this.getLocation());
        call.setParent(this.getParent());
        call.setOrigin(this);
        return call;
    }
    


    @Override
    public void updateRelationships(boolean deep) {
        this.renewChildren(this.left, this.op, this.right);
        this.left.setParent(this);
        this.left.setPropertyInParent(LEFT_OPERAND);
        if (deep) {
            this.left.updateRelationships(deep);
        }
        this.op.setParent(this);
        this.op.setPropertyInParent(OPERATOR);
        if (deep) {
            op.updateRelationships(deep);
        }
        this.right.setParent(this);
        this.right.setPropertyInParent(RIGHT_OPERAND);
        if (deep) {
            this.right.updateRelationships(deep);
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
        } else if (child == this.left) {
            if (!newChild.is(ASTExpression.class)) {
                throw new IllegalArgumentException(ERROR_NOT_EXPECTED_TYPE);
            }
            this.left = newChild.as(ASTExpression.class);
        } else if (child == this.right) {
            if (!newChild.is(ASTProductExpression.class)) {
                throw new IllegalArgumentException(ERROR_NOT_EXPECTED_TYPE);
            }
            this.right = newChild.as(ASTProductExpression.class);
        } else if (child == this.op) {
            if (!newChild.is(ASTOperator.class)) {
                throw new IllegalArgumentException(ERROR_NOT_EXPECTED_TYPE);
            }
            this.op = newChild.as(ASTOperator.class);
        } else {
            throw new IllegalArgumentException(ERROR_NOT_A_CHILD);
        }
        this.updateRelationships(false);
    }



    @Override
    public boolean accept(ASTVisitor visitor) {
        if (visitor.shouldVisitBinaryExpressions) {
            switch (visitor.visit(this)) {
            case PROCESS_SKIP:  return true;
            case PROCESS_ABORT: return false;
            }
        }

        if (!(this.left.accept(visitor) && this.right.accept(visitor) && 
                this.op.accept(visitor))) {
            return false;
        }

        if (visitor.shouldVisitBinaryExpressions) {
            return visitor.leave(this) != PROCESS_ABORT;
        }
        return true;
    }



    @Override
    public ASTExpression getLeftOperand() {
        return this.left;
    }



    @Override
    public void setLeftOperand(ASTExpression exp) {
        if (exp == null) {
            throw new NullPointerException("exp"); //$NON-NLS-1$
        } else if (exp == this) {
            throw new IllegalArgumentException(ERROR_SELF_AS_CHILD);
        }
        this.assertNotFrozen();
        this.assertNotFrozen(exp);
        this.left = exp;
        this.updateRelationships(false);
    }



    @Override
    public ASTExpression getRightOperand() {
        return this.right;
    }



    @Override
    public void setRightOperand(ASTExpression exp) {
        if (exp == null) {
            throw new NullPointerException("exp"); //$NON-NLS-1$
        } else if (exp == this) {
            throw new IllegalArgumentException(ERROR_SELF_AS_CHILD);
        }
        this.assertNotFrozen();
        this.assertNotFrozen(exp);
        this.right = exp;
        this.updateRelationships(false);
    }



    @Override
    public ASTOperator getOperator() {
        return this.op;
    }



    @Override
    public void setOperator(ASTOperator op) {
        if (op == null) {
            throw new NullPointerException("exp"); //$NON-NLS-1$
        }
        this.assertNotFrozen();
        this.assertNotFrozen(op);
        this.op = op;
        this.updateRelationships(false);
    }

    
    
    @Override
    public ASTBinaryExpression deepOrigin() {
        return super.deepOrigin().as(ASTBinaryExpression.class);
    }
    
    
    
    @Override
    public ASTBinaryExpression getOrigin() {
        return super.getOrigin().as(ASTBinaryExpression.class);
    }


    @Override
    public ASTBinaryExpressionImpl copy() {
        final ASTExpression leftCopy = this.left.copy();
        final ASTExpression rightCopy = this.right.copy();
        final ASTOperator opCopy = this.op.copy();
        final ASTBinaryExpressionImpl copy = this.getNodeFactory().newBinaryExpression(
                opCopy, leftCopy, rightCopy);
        copy.setLocation(this.getLocation());
        copy.setOrigin(this);
        copy.setSyntax(this.getSyntax());
        return copy;
    }
}
