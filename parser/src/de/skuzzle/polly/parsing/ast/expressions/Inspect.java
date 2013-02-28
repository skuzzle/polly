package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversal;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Transformation;
import de.skuzzle.polly.parsing.ast.visitor.ASTVisitor;


public class Inspect extends Expression {
    
    private static final long serialVersionUID = 1L;
    
    private final Expression access;
    private final boolean global;
    
    
    public Inspect(Position position, Expression access, boolean global) {
        super(position);
        this.access = access;
        this.global = global;
    }

    
    
    public boolean isGlobal() {
        return this.global;
    }
    

    
    public Expression getAccess() {
        return this.access;
    }
    
    

    @Override
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return visitor.visit(this);
    }
    
    
    
    @Override
    public Node transform(Transformation transformation) throws ASTTraversalException {
        return transformation.transformInspect(this);
    }
    
    
    
    @Override
    public boolean traverse(ASTTraversal visitor) throws ASTTraversalException {
        switch (visitor.before(this)) {
        case ASTTraversal.SKIP: return true;
        case ASTTraversal.ABORT: return false;
        }
        this.access.traverse(visitor);
        return visitor.after(this) == ASTTraversal.CONTINUE;
    }
}
