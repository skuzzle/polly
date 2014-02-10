package de.skuzzle.polly.core.parser.dom;

import de.skuzzle.parser.dom.ASTNode;
import de.skuzzle.polly.dom.ASTName;
import de.skuzzle.polly.dom.ASTParameter;
import de.skuzzle.polly.dom.ASTVisitor;

public class ASTParameterImpl extends AbstractPollyNode implements ASTParameter {

    private ASTName typeName;
    private ASTName name;



    ASTParameterImpl(ASTName typeName, ASTName name) {
        if (typeName == null) {
            throw new NullPointerException("typeName"); //$NON-NLS-1$
        } else if (name == null) {
            throw new NullPointerException("name"); //$NON-NLS-1$
        }
        assertNotFrozen(typeName);
        assertNotFrozen(name);
        this.typeName = typeName;
        this.name = name;
    }



    @Override
    public void updateRelationships(boolean deep) {
        this.renewChildren(this.typeName, this.name);
        this.typeName.setParent(this);
        this.typeName.setPropertyInParent(TYPE_NAME);
        if (deep) {
            this.typeName.updateRelationships(deep);
        }
        this.name.setParent(this);
        this.name.setPropertyInParent(PARAMETER_NAME);
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
        } else if (newChild == null) {
            throw new NullPointerException("newChild"); //$NON-NLS-1$
        } else if (this.getParent() != null && newChild == this.getParent()) {
            throw new IllegalArgumentException(ERROR_PARENT_AS_CHILD);
        } else if (child == this.typeName) {
            if (!newChild.is(ASTName.class)) {
                throw new IllegalArgumentException(ERROR_NOT_EXPECTED_TYPE);
            }
            this.typeName = newChild.as(ASTName.class);
        } else if (child == this.name) {
            if (!newChild.is(ASTName.class)) {
                throw new IllegalArgumentException(ERROR_NOT_EXPECTED_TYPE);
            }
            this.name = newChild.as(ASTName.class);
        } else {
            throw new IllegalArgumentException(ERROR_NOT_A_CHILD);
        }
        this.updateRelationships(false);
    }



    @Override
    public boolean accept(ASTVisitor visitor) {
        if (visitor.shouldVisitParameters) {
            switch (visitor.visit(this)) {
            case PROCESS_SKIP: return true;
            case PROCESS_ABORT: return false;
            }
        }
        
        if (!(this.typeName.accept(visitor) && this.name.accept(visitor))) {
            return false;
        }
        
        if (visitor.shouldVisitParameters) {
            return visitor.leave(this) != PROCESS_ABORT;
        }
        return true;
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
        assertNotFrozen();
        assertNotFrozen(name);
        this.name = name;
        this.updateRelationships(false);
    }



    @Override
    public ASTName getTypeName() {
        return this.typeName;
    }



    @Override
    public void setTypeName(ASTName type) {
        if (type == null) {
            throw new NullPointerException("type"); //$NON-NLS-1$
        }
        assertNotFrozen();
        assertNotFrozen(type);
        this.typeName = type;
        this.updateRelationships(false);
    }


    
    @Override
    public ASTParameter getOrigin() {
        return super.getOrigin().as(ASTParameter.class);
    }
    
    
    
    @Override
    public ASTParameter deepOrigin() {
        return super.deepOrigin().as(ASTParameter.class);
    }

    
    
    @Override
    public ASTParameter copy() {
        final ASTParameterImpl copy = getNodeFactory().newParameter(
                this.typeName.copy(), this.name.copy());
        copy.setOrigin(this);
        copy.setLocation(this.getLocation());
        copy.setSyntax(this.getSyntax());
        return copy;
    }
}
