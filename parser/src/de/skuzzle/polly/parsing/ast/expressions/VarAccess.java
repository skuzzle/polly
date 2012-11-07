package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;


public class VarAccess extends Expression {

    private final Identifier identifier;
    
    
    public VarAccess(Position position, Identifier identifier) {
        super(position);
        this.identifier = identifier;
    }
    
    
    
    public Identifier getIdentifier() {
        return this.identifier;
    }

    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitVarAccess(this);
    }
}
