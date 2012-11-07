package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;

/**
 * Assignments either declare new variables or new functions.
 * 
 * @author Simon Taddiken
 */
public class AssignmentExpression extends Expression {

    
    private final Expression expression;
    private final Declaration declaration;
    
    
    
    public AssignmentExpression(Position position, Expression expression, 
            Declaration declaration) {
        super(position);
        this.expression = expression;
        this.declaration = declaration;
    }
    
    
    
    /**
     * Gets the expression that should be assigned to a variable or function.
     * 
     * @return The expression.
     */
    public Expression getExpression() {
        return this.expression;
    }
    
    
    
    /**
     * Gets the declaration as which the expression should be stored.
     * 
     * @return The declaration.
     */
    public Declaration getDeclaration() {
        return this.declaration;
    }
    
    

    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitAssignment(this);
    }
}
