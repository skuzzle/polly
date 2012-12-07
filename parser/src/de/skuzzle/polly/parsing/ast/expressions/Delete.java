package de.skuzzle.polly.parsing.ast.expressions;

import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;


public class Delete extends Expression {

    private static final long serialVersionUID = 1L;
    
    private final List<Identifier> ids;
    
    
    
    public Delete(Position position, List<Identifier> ids) {
        super(position);
        this.ids = ids;
    }

    
    
    public List<Identifier> getIdentifiers() {
        return this.ids;
    }
    
    

    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitDelete(this);
    }
}
