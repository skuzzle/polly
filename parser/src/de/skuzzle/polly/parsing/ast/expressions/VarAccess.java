package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.Type;


public class VarAccess extends Expression {

    private static final long serialVersionUID = 1L;
    
    private ResolvableIdentifier identifier;
    private Type typeToResolve;
    
    
    
    public VarAccess(Position position, ResolvableIdentifier identifier) {
        super(position);
        if (identifier == null) {
            throw new NullPointerException("identifier is null");
        }
        this.identifier = identifier;
        this.typeToResolve = Type.ANY;
    }
    
    
    
    @Override
    public <T extends Node> void replaceChild(T current, T newChild) {
        if (current == this.identifier) {
            this.identifier = (ResolvableIdentifier) newChild;
        } else {
            super.replaceChild(current, newChild);
        }
    }

    
    
    
    public Type getTypeToResolve() {
        return this.typeToResolve;
    }
    
    
    
    public void setTypeToResolve(Type typeToResolve) {
        this.typeToResolve = typeToResolve;
    }
    
    
    
    public ResolvableIdentifier getIdentifier() {
        return this.identifier;
    }

    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitVarAccess(this);
    }
}
