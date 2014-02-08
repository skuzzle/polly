package de.skuzzle.polly.core.parser.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.skuzzle.parser.dom.ASTNode;
import de.skuzzle.polly.dom.ASTExpression;
import de.skuzzle.polly.dom.ASTProductExpression;
import de.skuzzle.polly.dom.ASTVisitor;

public class ASTProductExpressionImpl extends AbstractASTExpression implements
        ASTProductExpression {

    private final List<ASTExpression> expressions;



    ASTProductExpressionImpl() {
        this.expressions = new ArrayList<>();
    }



    @Override
    public void resolveType() {
        for (final ASTExpression expression : this.expressions) {
            expression.resolveType();
        }
    }



    @Override
    public void updateRelationships(boolean deep) {
        for (final ASTExpression expr : this.expressions) {
            expr.setParent(this);
            expr.setPropertyInParent(PART_OF_PRODUCT);
            if (deep) {
                expr.updateRelationships(deep);
            }
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
            throw new NullPointerException("newChild"); //$NON-NLS-1$
        } else if (this.getParent() != null && newChild == this.getParent()) {
            throw new IllegalArgumentException(ERROR_PARENT_AS_CHILD);
        } else if (!newChild.is(ASTExpression.class)) {
            throw new IllegalArgumentException(ERROR_NOT_EXPECTED_TYPE);
        }
        for (int i = 0; i < this.expressions.size(); ++i) {
            if (this.expressions.get(i) == child) {
                final ASTExpression expr = newChild.as(ASTExpression.class);
                expr.setPropertyInParent(PART_OF_PRODUCT);
                expr.setParent(this);
                this.expressions.set(i, expr);
                return;
            }
        }
        throw new IllegalArgumentException(ERROR_NOT_A_CHILD);
    }



    @Override
    public boolean accept(ASTVisitor visitor) {
        if (visitor.shouldVisitProducts) {
            switch (visitor.visit(this)) {
            case PROCESS_SKIP:  return true;
            case PROCESS_ABORT: return false;
            }
        }

        for (int i = 0; i < this.expressions.size(); ++i) {
            final ASTExpression child = this.expressions.get(i);
            if (!child.accept(visitor)) {
                return false;
            }
        }

        if (visitor.shouldVisitProducts) {
            return visitor.leave(this) != PROCESS_ABORT;
        }
        return true;
    }



    @Override
    public List<ASTExpression> getChildren() {
        return Collections.unmodifiableList(this.expressions);
    }



    @Override
    public List<ASTExpression> getExpressions() {
        return Collections.unmodifiableList(this.expressions);
    }



    @Override
    public void addExpression(ASTExpression expression) {
        if (expression == null) {
            throw new NullPointerException("expression"); //$NON-NLS-1$
        } else if (expression == this) {
            throw new IllegalArgumentException("can not add node to itself"); //$NON-NLS-1$
        }
        this.assertNotFrozen();
        this.assertNotFrozen(expression);
        this.expressions.add(expression);
        expression.setParent(this);
        expression.setPropertyInParent(PART_OF_PRODUCT);
    }


    
    @Override
    public ASTProductExpression getOrigin() {
        return super.getOrigin().as(ASTProductExpression.class);
    }
    
    
    
    @Override
    public ASTProductExpression deepOrigin() {
        return super.deepOrigin().as(ASTProductExpression.class);
    }
    
    

    @Override
    public ASTProductExpressionImpl copy() {
        final ASTProductExpressionImpl copy = getNodeFactory().newProduct();
        for (final ASTExpression expr : this.expressions) {
            copy.addExpression(expr.copy());
        }
        copy.setLocation(this.getLocation());
        copy.setOrigin(this);
        copy.setSyntax(this.getSyntax());
        return copy;
    }

}
