package de.skuzzle.polly.parsing.ast.declarations.types;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.declarations.TypeDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitable;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;


/**
 * <p>Base class for type expressions. Instances of this class are primitive types for
 * {@link Expression Expressions}. Using type constructors, more complex type structures
 * can be created, for example product types, list types and mapping types.</p>
 * 
 * <p>To determine whether two types are structural equal, you may use 
 * {@link #unify(Type, Type)}. This method will also find the correct substitute for
 * all {@link TypeVar TypeVars} within the checked type expressions of they are 
 * unifiable.</p>
 * 
 * <p>Please note that in order to be able to unify two type expressions that contain
 * type variables (as represented by a {@link TypeVar}) both expressions must use the
 * same instance of an equal variable in order to be considered unifiable.</p>
 * 
 * @author Simon Taddiken
 */
public class Type implements Serializable, Visitable<TypeVisitor>, Equatable {
    
    // XXX: static field order important!
    
    private static final long serialVersionUID = 1L;
    

    /** Primitive type for Numbers. */
    public final static Type NUM = new Type(new Identifier("Num"), true, true);
    
    /** Primitive type for dates. */
    public final static Type DATE = new Type(new Identifier("Date"), true, true);
    
    /** Primitive type for timespans. */
    public final static Type TIMESPAN = new Type(new Identifier("Timespan"), true, true);
    
    /** Primitive type for channels. */
    public final static Type CHANNEL = new Type(new Identifier("Channel"), true, true);
    
    /** Primitive type for users. */
    public final static Type USER = new Type(new Identifier("User"), true, true);
    
    /** Primitive type for strings. */
    public final static Type STRING = new Type(new Identifier("String"), true, true);
    
    /** Primitive type for booleans. */
    public final static Type BOOLEAN = new Type(new Identifier("Boolean"), true, true);
    
    /** Primitive type for help literals. */
    public final static Type HELP = new Type(new Identifier("Help"), true, true);
    
    /** Type indicating that a concrete type has not been resolved for an expression. */
    public final static Type UNKNOWN = new Type(new Identifier("UNKNOWN"), true, true);
    
    private final static Map<String, Type> primitives = new HashMap<String, Type>();
    static {
        primitives.put(NUM.getName().getId(), NUM);
        primitives.put(DATE.getName().getId(), DATE);
        primitives.put(TIMESPAN.getName().getId(), TIMESPAN);
        primitives.put(CHANNEL.getName().getId(), CHANNEL);
        primitives.put(USER.getName().getId(), USER);
        primitives.put(STRING.getName().getId(), STRING);
        primitives.put(BOOLEAN.getName().getId(), BOOLEAN);
        primitives.put(HELP.getName().getId(), HELP);
        primitives.put(UNKNOWN.getName().getId(), UNKNOWN);
    }
    
    
    
    /**
     * Tries to resolve a primitive type with the given name.
     * 
     * @param name Name of the type to resolve.
     * @return The resolved type.
     * @throws ASTTraversalException If no type with the given name exists.
     */
    public final static Type resolve(ResolvableIdentifier name) 
            throws ASTTraversalException {
        final Type t = primitives.get(name.getId());
        if (t == null) {
            throw new ASTTraversalException(name.getPosition(), 
                "Unbekannter Typ: " + name.getId());
        }
        return t;
    }
    
    
    /**
     * Checks whether the given type expression contains a type variable.
     * 
     * @param type The root of the type graph.
     * @return <code>true</code> if the type expression contains a type variable.
     */
    public final static boolean containsTypeVar(Type type) {
        return TypeVarFinder.containsTypeVar(type);
    }
    
    
    
    private static int varIds = 0;
    /**
     * Creates a new {@link TypeVar} with a name different from previous invocations of
     * this method.
     * 
     * @return A new {@link TypeVar}.
     */
    public final static TypeVar newTypeVar() {
        return newTypeVar("$" + (varIds++));
    }
    
    
    
    /**
     * Creates a {@link TypeVar} with the given name.
     * 
     * @param name The name of the type variable.
     * @return A new {@link TypeVar}.
     */
    public final static TypeVar newTypeVar(Identifier name) {
        return new TypeVar(name);
    }
    
    
    
    /**
     * Creates a {@link TypeVar} with given name.
     * 
     * @param name The name of the tyoe variable.
     * @return A new {@link TypeVar}.
     */
    public final static TypeVar newTypeVar(String name) {
        return newTypeVar(new Identifier(name));
    }
    
    
    
    /**
     * Tests for structural equality of the two given type expressions and substitutes
     * all {@link TypeVar TypeVars} with their resolved match if the expressions are
     * considered equal.
     * 
     * @param m Type to check.
     * @param n Type to check.
     * @return <code>true</code> iff both type expressions are structural equal.
     */
    public static boolean unify(Type m, Type n) {
        return unify(m, n, true);
    }
    
    
    
    /**
     * Tests for structural equality of the two given type expressions and lets you 
     * choose whether type variables should be resolved on success.
     * 
     * @param m Type to check.
     * @param n Type to check.
     * @param substitute Whether type variables should be substituted with their resolved
     *          match if both types are structural equal.
     * @return <code>true</code> iff both type expressions are structural equal.
     */
    public static boolean unify(Type m, Type n, boolean substitute) {
        final TypeUnifier tu = new TypeUnifier();
        return tu.unify(m, n, substitute);
    }
    
    
    
    private final Identifier name;
    private final boolean comparable;
    private final boolean primitve;
    protected Type parent;
    
    
    
    Type(Identifier name, boolean comparable, boolean primitive) {
        this.name = name;
        this.comparable = comparable;
        this.primitve = primitive;
        this.parent = null;
    }
    
    
    
    /**
     * Gets this types parent type. Will be <code>null</code> if this is the root of 
     * a type expression.
     * 
     * @return The parent type.
     */
    Type getParent() {
        return this.parent;
    }
    
    
    
    /**
     * Creates a {@link TypeDeclaration} for this primitive type. This may not be 
     * called on derived subclasses (like type constructors or type variables).
     * 
     * @return A {@link TypeDeclaration} for this primitive type.
     */
    public final TypeDeclaration declaration() {
        if (!this.isPrimitve()) {
            throw new IllegalStateException(
                "can not create declaration of none-primitive type");
        }
        return new TypeDeclaration(this.getName(), this);
    }
    
    
    
    /**
     * Gets whether this is a primitive type.
     * 
     * @return Whether this is a primitive type.
     */
    public final boolean isPrimitve() {
        return this.primitve;
    }
    
    
    
    /**
     * Gets the name of this type.
     * 
     * @return The type's name.
     */
    public Identifier getName() {
        return this.name;
    }
    
    
    
    /**
     * Gets whether literals of this type have a nature order.
     * 
     * @return Whether literals of this type have a nature order. 
     */
    public boolean isComparable() {
        return this.comparable;
    }
    
    
    
    @Override
    public String toString() {
        return this.getName().getId();
    }



    @Override
    public void visit(TypeVisitor visitor) {
        visitor.visitPrimitive(this);
    }
    
    
    
    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return Type.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        final Type other = (Type) o;
        return Type.unify(this, other, false);
    }
    
    
    
    /**
     * Replaces all {@link TypeVar TypeVars} in this type expression with new variables.
     * 
     * @return The modified type expression.
     */
    public Type fresh() {
        return this;
    }
    
    
    
    /**
     * Substitutes all occurrences of the given type variable with the given type.
     * 
     * @param var Variable to substitute.
     * @param t Type to substitute the variable with.
     * @return Same type expression but with substituted type variable.
     */
    public Type substitute(TypeVar var, Type t) {
        return this;
    }
    
    
    
    public Object readResolve() throws ObjectStreamException {
        if (this.isPrimitve()) {
            // HACK to maintain unique instances of primitive types though serialization
            try {
                return resolve(new ResolvableIdentifier(this.getName()));
            } catch (ASTTraversalException e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }
}
