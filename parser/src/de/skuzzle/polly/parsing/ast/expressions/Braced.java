package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.Type;


public class Braced extends Expression {

    private static final long serialVersionUID = 1L;
    
    private final Expression braced;
    
    
    
    public Braced(Expression braced) {
        super(braced.getPosition(), braced.getType());
        this.braced = braced;
    }
    
    
    @Override
    public Node getParent() {
        return this.braced.getParent();
    }
    
    
    
    @Override
    public void setParent(Node parent) {
        this.braced.setParent(parent);
    }
    
    
    
    @Override
    public Position getPosition() {
        return this.braced.getPosition();
    }
    
    
    
    @Override
    public void setType(Type type) {
        this.braced.setType(type);
    }
    
    
    
    @Override
    public Type getType() {
        return this.braced.getType();
    }
    
    

    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitBraced(this);
    }


    
    public Expression getExpression() {
        return this.braced;
    }
}
