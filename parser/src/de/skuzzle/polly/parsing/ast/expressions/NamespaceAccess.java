package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;


public class NamespaceAccess extends Expression {

    private final Identifier name;
    private final VarAccess rhs;
    
    
    
    public NamespaceAccess(Position position, Identifier name, VarAccess rhs) {
        super(position);
        this.name = name;
        this.rhs = rhs;
    }

    
    
    public Identifier getName() {
        return this.name;
    }
    
    
    
    public VarAccess getRhs() {
        return this.rhs;
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitAccess(this);
    }
}
