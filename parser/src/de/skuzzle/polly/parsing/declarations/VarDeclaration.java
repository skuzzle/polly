package de.skuzzle.polly.parsing.declarations;

import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.tree.Expression;
import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;


public class VarDeclaration extends Declaration {

    private static final long serialVersionUID = 1L;
    private Expression expression;
    
    public VarDeclaration(IdentifierLiteral id, boolean global, 
            boolean temp) {
        super(id, global, temp);
    }
    
    
    
    public VarDeclaration(IdentifierLiteral id, Type type) {
        super(id, false, false);
        this.setType(type);
    }
    
    
    
    public void setExpression(Expression expression) {
        this.expression = expression;
        this.setType(expression.getType());
    }
    
    
    
    public Expression getExpression() {
        return this.expression;
    }
    
    
    
    @Override
    public Object clone() {
        VarDeclaration result = new VarDeclaration(
            this.getName(),  // TODO: clone name? Might be impossibru because its clone method calls clone of declaration
            this.isGlobal(), this.isTemp());
        result.setExpression((Expression) this.getExpression().clone());
        return result;
    }
    
    
    
    @Override
    public String toString() {
        return "(VAR) " + (this.isGlobal() ? "global" : "") + this.getType() + " " + 
            this.getName();
    }
}
