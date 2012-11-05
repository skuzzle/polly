package de.skuzzle.polly.parsing.ast.declarations;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.types.Type;


public abstract class Declaration extends Node {

    private final Identifier name;
    private Type type;
    private boolean isGlobal;
    private boolean isTemp;
    
    
    public Declaration(Position position, Identifier name, Type type) {
        super(position);
        this.name = name;
        this.type = type;
    }
    
    
    
    public Type getType() {
        return this.type;
    }
    
    
    
    void setType(Type type) {
        this.type = type;
    }
    
    
    
    public Identifier getName() {
        return this.name;
    }


    
    public boolean isGlobal() {
        return this.isGlobal;
    }


    
    public void setGlobal(boolean isGlobal) {
        this.isGlobal = isGlobal;
    }


    
    public boolean isTemp() {
        return this.isTemp;
    }


    
    public void setTemp(boolean isTemp) {
        this.isTemp = isTemp;
    }
}