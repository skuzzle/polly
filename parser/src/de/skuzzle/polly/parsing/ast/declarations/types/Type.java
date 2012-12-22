package de.skuzzle.polly.parsing.ast.declarations.types;

import java.io.ObjectStreamException;
import java.io.Serializable;

import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.declarations.TypeDeclaration;
import de.skuzzle.polly.parsing.ast.declarations.Typespace;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitable;


public class Type implements Serializable, Visitable<TypeVisitor> {
    
    // XXX: static field order important!
    
    private static final long serialVersionUID = 1L;
    
    private static int varIds = 0;

    public final static Type NUM = new Type(new Identifier("Num"), true, true);
    public final static Type DATE = new Type(new Identifier("Date"), true, true);
    public final static Type TIMESPAN = new Type(new Identifier("Timespan"), true, true);
    public final static Type CHANNEL = new Type(new Identifier("Channel"), true, true);
    public final static Type USER = new Type(new Identifier("User"), true, true);
    public final static Type STRING = new Type(new Identifier("String"), true, true);
    public final static Type BOOLEAN = new Type(new Identifier("Boolean"), true, true);
    public final static Type HELP = new Type(new Identifier("Help"), true, true);
    public final static Type UNKNOWN = new Type(new Identifier("Unknown"), true, true);
    
    private final static Typespace TYPE_SPACE = new Typespace();
    
    
    
    public final static TypeVar newTypeVar() {
        return newTypeVar("$" + (varIds++));
    }
    
    
    
    public final static TypeVar newTypeVar(Identifier name) {
        return new TypeVar(name);
    }
    
    
    
    public final static TypeVar newTypeVar(String name) {
        return newTypeVar(new Identifier(name));
    }
    
    
    
    public static boolean unify(Type m, Type n) {
        return unify(m, n, true);
    }
    
    
    
    public static boolean unify(Type m, Type n, boolean substitute) {
        final TypeUnifier tu = new TypeUnifier();
        return tu.unify(m, n, substitute);
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
    
    
    
    Type getParent() {
        return this.parent;
    }
    
    
    
    public TypeDeclaration declaration() {
        return new TypeDeclaration(this.getName(), this);
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
    public String toString() {
        return this.getName().getId();
    }



    @Override
    public void visit(TypeVisitor visitor) {
        visitor.visitPrimitive(this);
    }
    
    
    
    public Object readResolve() throws ObjectStreamException {
        // HACK to maintain unique instances of primitive types though serialization
        try {
            return TYPE_SPACE.resolveType(new ResolvableIdentifier(this.getName()));
        } catch (ASTTraversalException e) {
            throw new RuntimeException(e);
        }
    }
}
