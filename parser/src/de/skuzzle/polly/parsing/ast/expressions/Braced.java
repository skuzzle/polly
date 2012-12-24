package de.skuzzle.polly.parsing.ast.expressions;

import java.util.Collection;
import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;

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
        super(braced.getPosition(), braced.getUnique());
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
    public void setUnique(Type type) {
        this.expression.setUnique(type);
    }
    
    
    
    @Override
    public Type getUnique() {
        return this.expression.getUnique();
    }
    
    
    
    @Override
    public void addType(Type type) {
        this.expression.addType(type);
    }
    
    
    
    @Override
    public void addTypes(Collection<Type> types) {
        this.expression.addTypes(types);
    }
    
    
    
    @Override
    public List<Type> getTypes() {
        return this.expression.getTypes();
    }
    

    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitBraced(this);
    }


    
    public Expression getExpression() {
        return this.expression;
    }
    
    
    
    @Override
    public String toString() {
        return "(" + this.expression.toString() + ")";
    }
}
