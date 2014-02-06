package de.skuzzle.polly.core.parser.dom;

import de.skuzzle.parser.dom.ASTNode;
import de.skuzzle.polly.dom.ASTExpression;
import de.skuzzle.polly.dom.ASTOperator;
import de.skuzzle.polly.dom.ASTUnaryExpression;
import de.skuzzle.polly.dom.ASTVisitor;

public class ASTUnaryExpressionImpl extends AbstractASTExpression implements
        ASTUnaryExpression {

    /** Operand of this expression */
    private ASTExpression operand;
    
    /** Operator of this expression */
    private ASTOperator operator;



    ASTUnaryExpressionImpl(ASTOperator op, ASTExpression operand) {
        if (op == null) {
            throw new NullPointerException("op"); //$NON-NLS-1$
        } else if (operand == null) {
            throw new NullPointerException("operand"); //$NON-NLS-1$
        }
        this.assertNotFrozen(operand);
        this.assertNotFrozen(op);

        this.operand = operand;
        this.operator = op;
    }



    @Override
    public void resolveType() {
    }



    @Override
    public void updateRelationships(boolean deep) {
        this.renewChildren(this.operand, this.operator);
        this.operand.setParent(this);
        this.operand.setPropertyInParent(OPERAND);
        if (deep) {
            this.operand.updateRelationships(deep);
        }
        
        this.operator.setParent(this);
        this.operator.setPropertyInParent(OPERATOR);
        if (deep) {
            this.operator.updateRelationships(deep);
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
        } else if (child == this.operand) {
            if (!newChild.is(ASTExpression.class)) {
                throw new IllegalArgumentException(ERROR_NOT_EXPECTED_TYPE);
            }
            this.operand = newChild.as(ASTExpression.class);
        } else if (child == this.operator) {
            if (!newChild.is(ASTOperator.class)) {
                throw new IllegalArgumentException(ERROR_NOT_EXPECTED_TYPE);
            }
            this.operator = newChild.as(ASTOperator.class);
        } else {
            throw new IllegalArgumentException(ERROR_NOT_A_CHILD);
        }
        this.updateRelationships(false);
    }



    @Override
    public boolean accept(ASTVisitor visitor) {
        if (!visitor.shouldVisitUnaryExpressions) {
            return true;
        }
        switch (visitor.visit(this)) {
        case PROCESS_SKIP: return true;
        case PROCESS_ABORT: return false;
        }
        
        if (!this.operand.accept(visitor)) {
            return false;
        }
        if (!this.operator.accept(visitor)) {
            return false;
        }
        
        return visitor.leave(this) != PROCESS_ABORT;
    }



    @Override
    public ASTCallExpressionImpl asCall() {
        final ASTExpression lhs = getNodeFactory().newIdExpression(this.operator.copy());
        final ASTProductExpressionImpl rhs = getNodeFactory().newProduct(
                this.operand.copy());
        final ASTCallExpressionImpl call = getNodeFactory().newCall(lhs, rhs);
        call.setParent(this.getParent());
        call.setLocation(this.getLocation());
        call.setOrigin(this);
        return call;
    }



    @Override
    public ASTExpression getOperand() {
        return this.operand;
    }



    @Override
    public void setOperand(ASTExpression expression) {
        if (expression == null) {
            throw new NullPointerException("expression"); //$NON-NLS-1$
        }
        this.assertNotFrozen();
        this.assertNotFrozen(expression);
        this.operand = expression;
        this.updateRelationships(false);
    }



    @Override
    public ASTOperator getOperator() {
        return this.operator;
    }



    @Override
    public void setOperator(ASTOperator op) {
        if (op == null) {
            throw new NullPointerException("op"); //$NON-NLS-1$
        }
        this.assertNotFrozen();
        this.assertNotFrozen(op);
        this.operator = op;
        this.updateRelationships(false);
    }



    @Override
    public ASTUnaryExpression getOrigin() {
        return super.getOrigin().as(ASTUnaryExpression.class);
    }



    @Override
    public ASTUnaryExpression deepOrigin() {
        return super.deepOrigin().as(ASTUnaryExpression.class);
    }



    @Override
    public ASTUnaryExpression copy() {
        final ASTUnaryExpressionImpl copy = getNodeFactory().newUnaryExpression(
                this.operator.copy(), this.operand.copy());
        copy.setLocation(this.getLocation());
        copy.setOrigin(this);
        return copy;
    }

}