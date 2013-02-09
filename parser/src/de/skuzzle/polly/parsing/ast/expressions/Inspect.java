package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Transformation;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;


public class Inspect extends Expression {
    
    private static final long serialVersionUID = 1L;
    
    private final ResolvableIdentifier name;
    
    
    public Inspect(Position position, ResolvableIdentifier name) {
        super(position);
        this.name = name;
    }


    
    public ResolvableIdentifier getName() {
        return this.name;
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
