package de.skuzzle.polly.parsing.ast.expressions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.declarations.types.TypeResolvedCallBack;
import de.skuzzle.polly.parsing.ast.declarations.types.TypeUnifier;
import de.skuzzle.polly.tools.Equatable;

/**
 * Super class for all expression Nodes in the AST.
 * 
 * @author Simon Taddiken
 */
public abstract class Expression extends Node {

    private static final long serialVersionUID = 1L;
    
    private Type unique;
    private final List<Type> types;
    private TypeResolvedCallBack callback;
    
    
    
    /**
     * Creates a new Expression with given {@link Position} and unique {@link Type}.
     * 
     * @param position Position within the input String of this expression.
     * @param unique Type of this expression.
     */
    public Expression(Position position, Type unique) {
        super(position);
        this.unique = unique;
        this.types = new ArrayList<Type>();
        if (unique != Type.UNKNOWN) {
            this.types.add(unique);
        }
    }
    
    
    
    /**
     * Sets a callback interface which will be notified whenever a possible type for this
     * expression has been resolved.
     * 
     * @param trcb The callback to set.
     */
    public void setTypeResolvedCallBack(TypeResolvedCallBack trcb) {
        this.callback = trcb;
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
     * Gets a list of all possible types for this expression. You should never modify the
     * returned list directly. Use {@link #addType(Type, TypeUnifier)} and 
     * {@link #addTypes(Collection, TypeUnifier)} to add possible types to this list.
     * 
     * @return List of possible types of this expression.
     */
    public List<Type> getTypes() {
        return this.types;
    }
    
    
    
    /**
     * Adds another type as possible type for this expression. If an instance of that 
     * type is already contained in the type list, the latter call will be ignored.
     * 
     * @param type Possible type of this expression.
     * @param unifier Current unification context.
     */
    public void addType(Type type, TypeUnifier unifier) {
        for (final Type t : this.types) {
            if (unifier.canUnify(t, type, false)) {
                return;
            }
        }
        if (this.callback != null) {
            this.callback.typeResolved(type, unifier);
        }
        this.types.add(type);
    }
    
    
    
    /**
     * Adds all the types from the given collection as possible type for this 
     * expression. Types for which an instance already exists in this expression's type 
     * list, will be ignored.
     * 
     * <p>This method simply calls {@link #addType(Type, TypeUnifier)} for each type in 
     * the given collection.</p>
     * @param types Types to add as possible type for this expression.
     * @param unifier Current unification context.
     */
    public void addTypes(Collection<? extends Type> types, TypeUnifier unifier) {
        for (final Type type : types) {
            this.addType(type, unifier);
        }
    }
    
    
    
    public void setTypes(Collection<Type> types, TypeUnifier unifier) {
        this.types.clear();
        this.addTypes(types, unifier);
    }

    
    
    /**
     * Gets whether the type of this expression was already resolved.
     * 
     * @return <code>true</code> if the type of this expression is not 
     *          {@link Type#UNKNOWN}.
     */
    public boolean typeResolved() {
        return this.getUnique() != Type.UNKNOWN;
    }
    
    
    
    /**
     * Gets the single resolved {@link Type} of this expression. If it has not yet been
     * resolved, it will be {@link Type#UNKNOWN}.
     * 
     * @return The type.
     */
    public Type getUnique() {
        return this.unique;
    }
    
    
    
    /**
     * Sets the unique type of this expression.
     * 
     * @param unique The new type.
     */
    public void setUnique(Type unique) {
        this.unique = unique;
    }

    
    
    @Override
    public Class<?> getEquivalenceClass() {
        return Expression.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        final Expression other = (Expression) o;
        return this.getUnique().equals(other.getUnique());
    }
    
    
    
    @Override
    public String toString() {
        return "unique: " + this.unique + ", types: " + this.types;
    }
}
