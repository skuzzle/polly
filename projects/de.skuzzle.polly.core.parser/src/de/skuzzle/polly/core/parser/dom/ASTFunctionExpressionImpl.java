package de.skuzzle.polly.core.parser.dom;

import java.util.ArrayList;
import java.util.List;

import de.skuzzle.parser.dom.ASTNode;
import de.skuzzle.polly.dom.ASTExpression;
import de.skuzzle.polly.dom.ASTFunctionExpression;
import de.skuzzle.polly.dom.ASTParameter;
import de.skuzzle.polly.dom.ASTPollyNode;
import de.skuzzle.polly.dom.ASTVisitor;


public class ASTFunctionExpressionImpl extends AbstractASTExpression 
        implements ASTFunctionExpression{

    /** Formal parameters of this function */
    private List<ASTParameter> parameters;
    
    /** Body expression of this function */
    private ASTExpression body;
    
    /** The logical scope of this function */
    private final ScopeImpl scope;
    
    
    
    public ASTFunctionExpressionImpl(ASTExpression body) {
        if (body == null) {
            throw new NullPointerException("body"); //$NON-NLS-1$
        }
        assertNotFrozen(body);
        this.scope = new ScopeImpl(null);
        this.body = body;
        this.parameters = new ArrayList<>();
        this.updateRelationships(false);
    }
    
    
    
    @Override
    public void resolveType() {
    }

    
    
    @Override
    public void updateRelationships(boolean deep) {
        final ASTPollyNode[] children = new ASTPollyNode[this.parameters.size() + 1];
        children[children.length - 1] = this.body;
        this.parameters.toArray(children);
        this.renewChildren(children);
        for (final ASTPollyNode node : children) {
            node.setParent(this);
            node.setPropertyInParent(PARAMETER);
            if (deep) {
                node.updateRelationships(deep);
            }
        }
        children[children.length - 1].setPropertyInParent(BODY);
    }

    
    
    @Override
    public void replaceChild(ASTNode<ASTVisitor> child, ASTNode<ASTVisitor> newChild) {
    }

    
    
    @Override
    public boolean accept(ASTVisitor visitor) {
        if (visitor.shouldVisitFunctions) {
            switch (visitor.visit(this)) {
            case PROCESS_SKIP: return true;
            case PROCESS_ABORT: return false;
            }
        }
        
        for (final ASTParameter param : this.parameters) {
            if (!param.accept(visitor)) {
                return false;
            }
        }
        if (!this.body.accept(visitor)) {
            return false;
        }
        
        if (visitor.shouldVisitFunctions) {
            return visitor.leave(this) != PROCESS_ABORT;
        }
        
        return true;
    }

    
    
    @Override
    public List<? extends ASTParameter> getParameters() {
        return this.parameters;
    }

    
    
    @Override
    public void addParameter(ASTParameter param) {
        if (param == null) {
            throw new NullPointerException("param"); //$NON-NLS-1$
        }
        assertNotFrozen();
        assertNotFrozen(param);
        this.parameters.add(param);
        this.updateRelationships(false);
    }

    
    
    @Override
    public ASTExpression getBody() {
        return this.body;
    }

    
    
    @Override
    public void setBody(ASTExpression body) {
        if (body == null) {
            throw new NullPointerException("body"); //$NON-NLS-1$
        }
        assertNotFrozen();
        assertNotFrozen(body);
        this.body = body;
        this.updateRelationships(false);
    }

    
    
    @Override
    public ScopeImpl getScope() {
        return this.scope;
    }

    

    @Override
    public ASTFunctionExpression getOrigin() {
        return super.getOrigin().as(ASTFunctionExpression.class);
    }
    
    
    
    @Override
    public ASTFunctionExpression deepOrigin() {
        return super.deepOrigin().as(ASTFunctionExpression.class);
    }
    
    
    
    @Override
    public ASTFunctionExpressionImpl copy() {
        final ASTFunctionExpressionImpl copy = getNodeFactory().newFunction(
                this.body.copy());
        copy.setOrigin(this);
        copy.setLocation(this.getLocation());
        copy.setSyntax(this.getSyntax());
        return copy;
    }
}
