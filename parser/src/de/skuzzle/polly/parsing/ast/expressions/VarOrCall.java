package de.skuzzle.polly.parsing.ast.expressions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.Visitor;
import de.skuzzle.polly.parsing.ast.declarations.FunctionDeclaration;


public class VarOrCall extends Expression {
    
    private final Identifier identifier;
    private final List<Expression> parameters;
    private boolean isOperator;
    
    
    
    public VarOrCall(Position position, Identifier identifier) {
        this(position, identifier, new ArrayList<Expression>());
    }
    
    

    public VarOrCall(Position position, Identifier identifier, 
            Collection<Expression> parameters) {
        super(position);
        this.identifier = identifier;
        this.parameters = new ArrayList<Expression>(parameters);
    }
    
    
    
    
    public void setOperator(boolean isOperator) {
        this.isOperator = isOperator;
    }
    
    
    
    public boolean isOperator() {
        return this.isOperator;
    }
    
    
    
    public boolean isCall() {
        return this.identifier.getDeclaration() instanceof FunctionDeclaration;
    }
    
    
    
    public boolean isVarAccess() {
        return !this.isCall();
    }

    
    
    public Identifier getIdentifier() {
        return this.identifier;
    }
    
    
    
    public List<Expression> getParameters() {
        return this.parameters;
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitVarOrCall(this);
    }
}
