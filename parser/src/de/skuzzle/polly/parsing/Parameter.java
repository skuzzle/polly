package de.skuzzle.polly.parsing;

import java.io.Serializable;

import de.skuzzle.polly.parsing.tree.Expression;
import de.skuzzle.polly.parsing.tree.IdentifierLiteral;

public class Parameter implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Expression type;
    private IdentifierLiteral name;
    
    public Parameter(Expression type, String name) {
        this.type = type;
        this.name = new IdentifierLiteral(name);
    }
    
    
    
    public Type getType() {
        return this.type.getType();
    }
    
    
    public Expression getTypeExpression() {
        return this.type;
    }
    
    
    
    public IdentifierLiteral getName() {
        return this.name;
    }
}
