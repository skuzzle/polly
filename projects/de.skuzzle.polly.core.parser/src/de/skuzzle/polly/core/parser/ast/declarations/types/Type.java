package de.skuzzle.polly.core.parser.ast.declarations.types;

import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.core.parser.ParseException;
import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.Identifier;
import de.skuzzle.polly.core.parser.ast.ResolvableIdentifier;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.visitor.Visitable;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;


/**
 * <p>Base class for type expressions. Instances of this class are primitive types for
 * {@link Expression Expressions}. Using type constructors, more complex type structures
 * can be created, for example product types, list types and mapping types.</p>
 * 
 * <p>Please note that in order to be able to unify two type expressions that contain
 * type variables (as represented by a {@link TypeVar}) both expressions must use the
 * same instance for the same variable in order to be considered unifiable.</p>
 * 
 * @author Simon Taddiken
 */
public class Type implements Visitable<TypeVisitor>, Equatable {
    
    // XXX: static field order important!

    /** Primitive type for Numbers. */
    public final static Type NUM = new Type(new Identifier("num"), true, true);
    
    /** Primitive type for dates. */
    public final static Type DATE = new Type(new Identifier("date"), true, true);
    
    /** Primitive type for timespans. */
    public final static Type TIMESPAN = new Type(new Identifier("timespan"), true, true);
    
    /** Primitive type for channels. */
    public final static Type CHANNEL = new Type(new Identifier("channel"), true, true);
    
    /** Primitive type for users. */
    public final static Type USER = new Type(new Identifier("user"), true, true);
    
    /** Primitive type for strings. */
    public final static Type STRING = new Type(new Identifier("string"), true, true);
    
    /** Primitive type for booleans. */
    public final static Type BOOLEAN = new Type(new Identifier("boolean"), true, true);
    
    /** Primitive type for help literals. */
    public final static Type HELP = new Type(new Identifier("Help"), true, true);
    
    /** Type indicating that a concrete type has not been resolved for an expression. */
    public final static Type UNKNOWN = new Type(new Identifier("UNKNOWN"), true, true);
    
    private final static Map<String, Type> primitives = new HashMap<String, Type>();
    private final static Map<String, Type> typeVars = new HashMap<String, Type>();
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
     * Tries to resolve a primitive type with the given name. If no such type exists, a
     * type variable with the given name is returned (only if polymorph declarations are
     * allowed). 
     * 
     * @param name Name of the type to resolve.
     * @param allowPolymorph Whether polymorphic declarations are allowed.
     * @return The resolved type or <code>null</code> if polymorphic types are not 
     *          allowed and not type with given name exists.
     */
    public final static Type resolve(Identifier name, boolean allowPolymorph) {
        Type t = primitives.get(name.getId());
        if (t == null && allowPolymorph) {
            t = typeVars.get(name.getId());
            if (t == null) {
                t = new TypeVar(name);
                typeVars.put(name.getId(), t);
            }
        } else if (t == null && !allowPolymorph) {
            return null;
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
    
    
    
    /**
     * Gets an unique name for a new type variable.
     * 
     * @return Unique type variable name.
     */
    public final static Identifier nextTypeVarName() {
        return new Identifier(Position.NONE, "T_" + (varIds++));
    }
    
    
    
    private static int varIds = 0;
    /**
     * Creates a new {@link TypeVar} with a name different from previous invocations of
     * this method.
     * 
     * @return A new {@link TypeVar}.
     */
    public final static TypeVar newTypeVar() {
        return newTypeVar(nextTypeVarName());
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
     * @param name The name of the type variable.
     * @return A new {@link TypeVar}.
     */
    public final static TypeVar newTypeVar(String name) {
        return newTypeVar(new Identifier(name));
    }
    
    
    
    private final static TypeUnifier unifier = new TypeUnifier();
    
    /**
     * Tests whether the left type is an instance of the right type using unification.
     * 
     * @param left The left type expression.
     * @param right The right type expression.
     * @return Whether both types are unifiable.
     */
    public final static boolean tryUnify(Type left, Type right) {
        return unifier.tryUnify(left, right);
    }
    
    
    
    /**
     * Tests whether the left type is an instance of the right type using unification. If
     * successful, this method returns a {@link Substitution} for the occurring type
     * variables in the given type expressions.
     * 
     * @param left The left type expression.
     * @param right The right type expression.
     * @return A {@link Substitution} instance if unification was successful, 
     *          <code>null</code> otherwise.
     */
    public final static Substitution unify(Type left, Type right) {
        return unifier.unify(left, right);
    }
    
    
    
    private final Identifier name;
    private final boolean comparable;
    private final boolean primitve;
    
    
    /**
     * Creates a new simple type.
     * 
     * @param name String representation of the type as an {@link Identifier}.
     * @param comparable Whether literals of this type are comparable.
     * @param primitive Whether this represents a primitive type.
     */
    Type(Identifier name, boolean comparable, boolean primitive) {
        this.name = name;
        this.comparable = comparable;
        this.primitve = primitive;
    }
    
    
    
    /**
     * Creates a mapping type expression from this one to the given target type 
     * expression.
     * 
     * @param target The target (right side) of the mapping.
     * @return A new mapping type expression
     */
    public MapType mapTo(Type target) {
        return new MapType(this, target);
    }
    
    
    
    /**
     * Creates a mapping type from the given type expression to this one.
     * 
     * @param source The source (left side) of the mapping.
     * @return A new mapping type expression.
     */
    public MapType mapFrom(Type source) {
        return new MapType(source, this);
    }
    
    
    
    /**
     * Returns a new {@link ListType} expression with this type as a sub type.
     * @return A new {@link ListType} expression.
     */
    public ListType listOf() {
        return new ListType(this);
    }
    
    
    
    /**
     * Applies the given substitution to this type expression. This will create a new
     * type expression where all type variables are substituted by the rules implemented
     * in {@link Substitution#getSubstitute(TypeVar)}. Every other type expression
     * (except primitive types) will be recreated.
     * 
     * @param s The substitution to apply.
     * @return A new type expression.
     */
    public Type subst(Substitution s) {
        return this;
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
    public boolean visit(TypeVisitor visitor) {
        return visitor.visit(this);
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
        // Types are equal if they can be unified.
        final Type other = (Type) o;
        return tryUnify(this, other);
    }
}
