package de.skuzzle.polly.parsing.ast.declarations;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.Visitor;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;


public class VarDeclaration extends Declaration {

    private final Expression expression;
    
    
    
    public VarDeclaration(Position position, Identifier name, Expression expression) {
        super(position, name, expression.getType());
        this.expression = expression;
    }

    
    
    public Expression getExpression() {
        return this.expression;
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitVarDecl(this);
    }
}
