package de.skuzzle.polly.parsing.ast.declarations.types;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.declarations.TypeDeclaration;
import de.skuzzle.polly.parsing.ast.visitor.Visitable;


public class Type implements Serializable, Visitable<TypeVisitor> {
    
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
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException("do not compare types using #equals()");
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
