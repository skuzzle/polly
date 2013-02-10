package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Transformation;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;


public class Inspect extends Expression {
    
    private static final long serialVersionUID = 1L;
    
    private final Expression access;
    
    
    public Inspect(Position position, Expression access) {
        super(position);
        this.access = access;
    }


    
    public Expression getAccess() {
        return this.access;
    }
    
    

    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitInspect(this);
    }
    
    
    
    @Override
    public Node transform(Transformation transformation) throws ASTTraversalException {
        return transformation.transformInspect(this);
    }
}
