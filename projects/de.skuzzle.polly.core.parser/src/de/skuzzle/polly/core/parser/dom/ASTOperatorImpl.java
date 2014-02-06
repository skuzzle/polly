package de.skuzzle.polly.core.parser.dom;

import de.skuzzle.parser.dom.ASTNode;
import de.skuzzle.polly.dom.ASTOperator;
import de.skuzzle.polly.dom.ASTVisitor;

public class ASTOperatorImpl extends AbstractPollyNode implements ASTOperator {

    public static String typeToName(int type) {
        // TODO: add types
        return ""; //$NON-NLS-1$
    }
    
    
    /** The operator's type */
    private int type;
    
    /** The name to that type */
    private String name;
    
    /** The kind */
    private OperatorKind kind;
    
    
    
    ASTOperatorImpl(int type, OperatorKind kind) {
        if (kind == null) {
            throw new NullPointerException("kind"); //$NON-NLS-1$
        }
        this.type = type;
        this.name = typeToName(type);
        this.kind = kind;
    }
    
    
    
    @Override
    public OperatorKind getKind() {
        return this.kind;
    }
    
    
    
    @Override
    public void setKind(OperatorKind kind) {
        if (kind == null) {
            throw new NullPointerException("kind"); //$NON-NLS-1$
        }
        this.kind = kind;
    }
    
    
    
    @Override
    public String getName() {
        return this.name;
    }



    @Override
    public void updateRelationships(boolean deep) {
        // nothing to do here
    }



    @Override
    public void replaceChild(ASTNode<ASTVisitor> child, ASTNode<ASTVisitor> newChild) {
        throw new UnsupportedOperationException("operator has no parent"); //$NON-NLS-1$
    }



    @Override
    public boolean accept(ASTVisitor visitor) {
        if (!visitor.shouldVisitOperators) {
            return true;
        }
        
        switch (visitor.visit(this)) {
        case PROCESS_SKIP: return true;
        case PROCESS_ABORT: return false;
        }
        
        return visitor.leave(this) != PROCESS_ABORT;
    }



    @Override
    public int getType() {
        return this.type;
    }



    @Override
    public void setType(int type) {
        this.type = type;
        this.name = typeToName(type);
    }



    @Override
    public ASTOperator getOrigin() {
        return super.getOrigin().as(ASTOperator.class);
    }



    @Override
    public ASTOperator deepOrigin() {
        return super.deepOrigin().as(ASTOperator.class);
    }



    @Override
    public ASTOperatorImpl copy() {
        final ASTOperatorImpl copy = getNodeFactory().newOperator(this.type, this.kind);
        copy.setLocation(this.getLocation());
        copy.setOrigin(this);
        return copy;
    }
}