package de.skuzzle.polly.parsing.ast.declarations;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;


public class VarDeclaration extends Declaration {

    private static final long serialVersionUID = 1L;
    
    private Expression expression;
    
    
    
    public VarDeclaration(Position position, Identifier name, 
            Expression expression) {
        super(position, name);
        this.expression = expression;
    }

    
    
    @Override
    public Type getType() {
        return this.expression.getUnique();
    }
    
    
    
    public Expression getExpression() {
        return this.expression;
    }
    
    
    
    @Override
    public <T extends Node> void replaceChild(T current, T newChild) {
        if (current == this.expression) {
            this.expression = (Expression) newChild;
        } else {
            super.replaceChild(current, newChild);
        }
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitVarDecl(this);
    }
    
    
    
    @Override
    public String toString() {
        return "[VarDeclaration: id=" + this.getName() + ", type=" + this.getType() + "]";
    }
}
