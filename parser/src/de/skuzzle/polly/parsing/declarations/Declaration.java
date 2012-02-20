package de.skuzzle.polly.parsing.declarations;

import java.io.Serializable;

import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;


/**
 * A Declaration contains the right subtree of the '->' operator.
 * 
 * @author Simon
 *
 */
public abstract class Declaration implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private IdentifierLiteral name;
    private Type type;
    private boolean global;
    private boolean temp;
    
    
    public Declaration(IdentifierLiteral name, boolean global, boolean temp) {
        this.name = name;
        this.global = global;
        this.temp = temp;
    }
    
    
    
    
    public IdentifierLiteral getName() {
        return this.name;
    }
    
    
    
    public Type getType() {
        return this.type;
    }
    
    
    
    public void setType(Type type) {
        this.type = type;
    }
    
    
    
    
    public boolean isGlobal() {
        return this.global;
    }
    
    
    
    public boolean isTemp() {
        return this.temp;
    }
}