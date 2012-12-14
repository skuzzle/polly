package de.skuzzle.polly.parsing.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.types.Type;
import de.skuzzle.polly.tools.Equatable;

/**
 * Super class for all expression Nodes in the AST.
 * 
 * @author Simon Taddiken
 */
public abstract class Expression extends Node {

    private static final long serialVersionUID = 1L;
    
    private Type type;
    public List<Type> possibleTypes;
    
    
    
    /**
     * Creates a new Expression with given {@link Position} and {@link Type}.
     * 
     * @param position Position within the input String of this expression.
     * @param type Type of this expression.
     */
    public Expression(Position position, Type type) {
        super(position);
        this.type = type;
        this.possibleTypes = new ArrayList<Type>();
    }
    
    
    
    /**
     * Creates a new Expression with given {@link Position} and unknown type.
     * 
     * @param position Position within the input String of this expression.
     */
    public Expression(Position position) {
        this(position, Type.UNKNOWN);
    }
    
    
    
    public void addPossibleType(Type type) {
        if (this.typeResolved()) {
            throw new IllegalStateException(
                "can not add possile type because type was resolved");
        }
        this.possibleTypes.add(type);
    }

    
    
    /**
     * Gets whether the type of this expression was already resolved.
     * 
     * @return <code>true</code> if the type of this expression is not 
     *          {@link Type#UNKNOWN}.
     */
    public boolean typeResolved() {
        return this.possibleTypes.isEmpty() && this.getType() != Type.UNKNOWN;
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

    
    
    @Override
    public Class<?> getEquivalenceClass() {
        return Expression.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        final Expression other = (Expression) o;
        return this.getType().check(other.getType());
    }
}
