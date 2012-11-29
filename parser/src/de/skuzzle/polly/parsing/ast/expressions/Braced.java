package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.Type;

/**
 * Encapsulates another expression for the sole reason to represent that that expression
 * was entered in braces. All changes made to this expressions are delegated to the 
 * encapsulated expression.
 * 
 * @author Simon Taddiken
 */
public class Braced extends Expression {

    private static final long serialVersionUID = 1L;
    
    private Expression expression;
    
    
    
    public Braced(Expression braced) {
        super(braced.getPosition(), braced.getType());
        this.expression = braced;
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
    public Position getPosition() {
        return this.expression.getPosition();
    }
    
    
    
    @Override
    public void setType(Type type) {
        this.expression.setType(type);
    }
    
    
    
    @Override
    public Type getType() {
        return this.expression.getType();
    }
    
    

    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitBraced(this);
    }


    
    public Expression getExpression() {
        return this.expression;
    }
}
