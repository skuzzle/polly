package de.skuzzle.polly.core.parser.dom;

import de.skuzzle.parser.dom.ASTNode;
import de.skuzzle.polly.dom.ASTName;
import de.skuzzle.polly.dom.ASTVisitor;

public class ASTNameImpl extends AbstractPollyNode implements ASTName {

    private String name;
    
    
    
    ASTNameImpl(String name) {
        if (name == null) {
            throw new NullPointerException("name"); //$NON-NLS-1$
        }
        this.setName(name);
    }
    
    
    
    @Override
    public void updateRelationships(boolean deep) {}



    @Override
    public void replaceChild(ASTNode<ASTVisitor> child, ASTNode<ASTVisitor> newChild) {
        throw new UnsupportedOperationException("ASTName has no children"); //$NON-NLS-1$
    }



    @Override
    public boolean accept(ASTVisitor visitor) {
        if (visitor.shouldVisitNormalNames) {
            switch (visitor.visit(this)) {
            case PROCESS_SKIP:  return true;
            case PROCESS_ABORT: return false;
            }
            
            return visitor.leave(this) != PROCESS_ABORT;
        }
        return true;
    }



    @Override
    public String getName() {
        return this.name;
    }

    
    
    public void setName(String name) {
        this.assertNotFrozen();
        this.name = name;
    }
    

    
    @Override
    public ASTName getOrigin() {
        return super.getOrigin().as(ASTName.class);
    }
    
    
    
    @Override
    public ASTName deepOrigin() {
        return super.deepOrigin().as(ASTName.class);
    }
    
    
    
    @Override
    public ASTNameImpl copy() {
        final ASTNameImpl copy = this.getNodeFactory().newName(this.name);
        copy.setLocation(this.getLocation());
        copy.setOrigin(this);
        copy.setSyntax(this.getSyntax());
        return copy;
    }
}
