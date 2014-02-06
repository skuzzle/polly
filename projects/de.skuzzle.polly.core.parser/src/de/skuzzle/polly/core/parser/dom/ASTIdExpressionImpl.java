package de.skuzzle.polly.core.parser.dom;

import de.skuzzle.parser.dom.ASTNode;
import de.skuzzle.polly.dom.ASTIdExpression;
import de.skuzzle.polly.dom.ASTName;
import de.skuzzle.polly.dom.ASTVisitor;

public class ASTIdExpressionImpl extends AbstractASTExpression 
        implements ASTIdExpression {

    /** The name of this expression */
    private ASTName name;



    ASTIdExpressionImpl(ASTName name) {
        if (name == null) {
            throw new NullPointerException("name"); //$NON-NLS-1$
        }
        this.assertNotFrozen(name);
        this.name = name;
        this.updateRelationships(false);
    }



    @Override
    public void resolveType() {
        
    }


    
    @Override
    public void updateRelationships(boolean deep) {
        this.name.setParent(this);
        this.name.setPropertyInParent(ID_EXPRESSION_NAME);
        this.renewChildren(this.name);
        if (deep) {
            this.name.updateRelationships(deep);
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
        } else if (this.getParent() != null && newChild == this.getParent()) {
            throw new IllegalArgumentException(ERROR_PARENT_AS_CHILD);
        } else if (!newChild.is(ASTName.class)) {
            throw new IllegalArgumentException(ERROR_NOT_EXPECTED_TYPE);
        } else if (child == this.name) {
            this.name = newChild.as(ASTName.class);
        } else {
            throw new IllegalArgumentException(ERROR_NOT_A_CHILD);
        }
        this.updateRelationships(false);
    }



    @Override
    public boolean accept(ASTVisitor visitor) {
        switch (visitor.visit(this)) {
        case PROCESS_SKIP:  return true;
        case PROCESS_ABORT: return false;
        }
        
        if (this.name != null && !this.name.accept(visitor)) {
            return false;
        }
        
        return visitor.leave(this) != PROCESS_ABORT;
    }



    @Override
    public ASTName getName() {
        return this.name;
    }



    @Override
    public void setName(ASTName name) {
        if (name == null) {
            throw new NullPointerException("name"); //$NON-NLS-1$
        }
        this.assertNotFrozen();
        this.assertNotFrozen(name);

        this.name = name;
        this.updateRelationships(false);
    }

    
    
    @Override
    public ASTIdExpression getOrigin() {
        return super.getOrigin().as(ASTIdExpression.class);
    }
    

    
    @Override
    public ASTIdExpression deepOrigin() {
        return super.deepOrigin().as(ASTIdExpression.class);
    }
    
    

    @Override
    public ASTIdExpressionImpl copy() {
        final ASTIdExpressionImpl copy = getNodeFactory().newIdExpression(
                this.name.copy());
        copy.setLocation(this.getLocation());
        copy.setOrigin(this);
        return copy;
    }
}
