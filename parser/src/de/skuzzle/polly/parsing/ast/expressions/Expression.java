package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.types.Type;

/**
 * Super class for all expression Nodes in the AST.
 * 
 * @author Simon Taddiken
 */
public abstract class Expression extends Node {

    private Type type;
    private Type typeToResolve;
    
    /**
     * Creates a new Expression with given {@link Position} and {@link Type}.
     * 
     * @param position Position within the input String of this expression.
     * @param type Type of this expression.
     */
    public Expression(Position position, Type type) {
        super(position);
        this.type = type;
        this.typeToResolve = Type.ANY;
    }
    
    
    
    /**
     * Creates a new Expression with given {@link Position} and unknown type.
     * 
     * @param position Position within the input String of this expression.
     */
    public Expression(Position position) {
        this(position, Type.UNKNOWN);
    }

    
    
    /**
     * Gets the {@link Type} of this expression.
     * 
     * @return The type.
     */
    public Type getType() {
        return this.type;
    }
    
    
    
    /**
     * Sets the type of this expression.
     * 
     * @param type The new type.
     */
    public void setType(Type type) {
        this.type = type;
    }
    
    
    
    public Type getTypeToResolve() {
        return this.typeToResolve;
    }
    
    
    
    public void setTypeToResolve(Type typeToResolve) {
        this.typeToResolve = typeToResolve;
    }
}
