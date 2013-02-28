package de.skuzzle.polly.parsing.ast.expressions;

import java.util.Collection;
import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Transformation;
import de.skuzzle.polly.parsing.ast.visitor.ASTVisitor;

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
    
    
    
    public Braced(Position position, Expression braced) {
        super(position, braced.getUnique());
        this.expression = braced;
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
    public boolean addType(Type type) {
        return this.expression.addType(type);
    }
    
    
    
    @Override
    public void addTypes(Collection<? extends Type> types) {
        this.expression.addTypes(types);
    }
    
    
    
    @Override
    public List<Type> getTypes() {
        return this.expression.getTypes();
    }
    

    
    @Override
    public void visit(ASTVisitor visitor) throws ASTTraversalException {
        visitor.visitBraced(this);
    }
    
    
    
    @Override
    public Expression transform(Transformation transformation) 
            throws ASTTraversalException {
        return transformation.transformBraced(this);
    }


    
    public Expression getExpression() {
        return this.expression;
    }
    
    
    
    @Override
    public String toString() {
        return "(" + this.expression.toString() + ")";
    }
}
