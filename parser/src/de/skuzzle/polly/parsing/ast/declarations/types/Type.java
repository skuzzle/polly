package de.skuzzle.polly.parsing.ast.declarations.types;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.visitor.Visitable;
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
public class Type implements Serializable, Visitable<TypeVisitor>, Equatable {
    
    // XXX: static field order important!
    
    private static final long serialVersionUID = 1L;
    

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
     * type variable with the given name is returned. 
     * 
     * @param name Name of the type to resolve.
     * @return The resolved type.
     */
    public final static Type resolve(ResolvableIdentifier name) {
        Type t = primitives.get(name.getId());
        if (t == null) {
            t = typeVars.get(name.getId());
            if (t == null) {
                t = new TypeVar(name);
                typeVars.put(name.getId(), t);
            }
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
     * @param name The name of the tyoe variable.
     * @return A new {@link TypeVar}.
     */
    public final static TypeVar newTypeVar(String name) {
        return newTypeVar(new Identifier(name));
    }
    
    
    
    private final Identifier name;
    private final boolean comparable;
    private final boolean primitve;
    
    
    Type(Identifier name, boolean comparable, boolean primitive) {
        this.name = name;
        this.comparable = comparable;
        this.primitve = primitive;
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
        // TODO HACK XXX FIXME: implement equals 
        return o == this;
    }
    
    
    
    public Object readResolve() throws ObjectStreamException {
        if (this.isPrimitve()) {
            // HACK to maintain unique instances of primitive types though serialization
            return resolve(new ResolvableIdentifier(this.getName()));
        }
        return this;
    }
}
