package de.skuzzle.polly.parsing.ast.declarations;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.Type;


public class VarDeclaration extends Declaration {

    private final Expression expression;
    
    
    
    public VarDeclaration(Position position, Identifier name, 
            Expression expression) {
        super(position, name);
        this.expression = expression;
    }

    
    
    @Override
    public Type getType() {
        return this.expression.getType();
    }
    
    
    
    public Expression getExpression() {
        return this.expression;
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
