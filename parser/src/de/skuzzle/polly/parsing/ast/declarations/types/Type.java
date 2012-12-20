package de.skuzzle.polly.parsing.ast.declarations.types;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.declarations.TypeDeclaration;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitable;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;


public class Type implements Equatable, Serializable, Visitable<TypeVisitor> {
    
    private static final long serialVersionUID = 1L;
    
    public final static Type NUM = new Type(new Identifier("Num"), true, true);
    public final static Type DATE = new Type(new Identifier("Date"), true, true);
    public final static Type TIMESPAN = new Type(new Identifier("Timespan"), true, true);
    public final static Type CHANNEL = new Type(new Identifier("Channel"), true, true);
    public final static Type USER = new Type(new Identifier("User"), true, true);
    public final static Type STRING = new Type(new Identifier("String"), true, true);
    public final static Type BOOLEAN = new Type(new Identifier("Boolean"), true, true);
    public final static Type UNKNOWN = new Type(new Identifier("Unknown"), true, true);
    
    
    private final static Map<String, TypeVar> typeVars = new HashMap<String, TypeVar>();
    private static int varIds = 0;
    
    
    
    public final static TypeVar newTypeVar() {
        final Identifier id = new Identifier("$" + (varIds++));
        return new TypeVar(id);
    }
    
    
    
    public final static TypeVar newTypeVar(Identifier name) {
        TypeVar v = typeVars.get(name.getId());
        if (v == null) {
            v = new TypeVar(name);
            typeVars.put(name.getId(), v);
        }
        return v;
    }
    
    
    
    public final static TypeVar newTypeVar(String name) {
        return newTypeVar(new Identifier(name));
    }
    
    
    public static boolean unify(Type m, Type n) {
        final TypeUnifier tu = new TypeUnifier();
        return tu.unify(m, n);
    }
    
    
    private final Identifier name;
    private final boolean comparable;
    private final boolean primitve;
    protected Type parent;
    
    
    
    public Type(Identifier name, boolean comparable, boolean primitive) {
        this.name = name;
        this.comparable = comparable;
        this.primitve = primitive;
        this.parent = null;
    }
    
    
    
    public final TypeDeclaration declaration() {
        return new TypeDeclaration(this.name, this);
    }

    
    
    protected void substituteTypeVar(TypeVar var, Type type) 
        throws ASTTraversalException {}
    
    
    
    protected boolean canSubstitute(TypeVar var, Type type) {
        return true;
    }
    

    /**
     * <p>Determines whether this type expression is unifiable with the given one. In 
     * other terms, determines whether the given type is an <i>instance</i> of this type 
     * expression.</p>
     * 
     * <p>If <code>unify</code> is <code>true</code>, and a {@link TypeVar} is hit on the 
     * way of unifying sub types, and it was not already substituted, we have found a 
     * proper substitute for that type variable. So
     * from now, that particular variable represents the substituted type within this
     * expressions. That means, that all other occurrences of that TypeVar in this
     * type expression must be substituted as well. An error may arise here, if we hit
     * on a TypeVar with the same name which has already been substituted with another
     * type.</p>
     * 
     * @param other Type to unify with this.
     * @param unify If true, all {@link TypeVar} instances in this type expression will
     *          be substituted.
     * @return <code>true</code> iff the given type expression is an instance of the 
     *          type expression represented by this.
     * @throws ASTTraversalException If attempting to substitute a TypeVar which has
     *          already been substituted. 
     */
    public boolean isUnifiableWith(Type other, boolean unify) 
            throws ASTTraversalException {
        // this is a primitive, so its unifiable if other is a primitive too
        return other.isPrimitve() && this.equals(other);  
    }
    
    
    
    public final boolean isPrimitve() {
        return this.primitve;
    }
    
    
    
    public Identifier getName() {
        return this.name;
    }
    
    
    
    public boolean isComparable() {
        return this.comparable;
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
        return false;
    }
    
    
    
    @Override
    public String toString() {
        return this.getName().getId();
    }



    @Override
    public void visit(TypeVisitor visitor) {
        visitor.visitPrimitive(this);
    }
}
