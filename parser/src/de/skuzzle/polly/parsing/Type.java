package de.skuzzle.polly.parsing;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;


public class Type implements Serializable {
       
    
    private static final long serialVersionUID = 1L;
    
    /*
     * Primitive types
     */
    public final static Type CHANNEL = new Type(new IdentifierLiteral("Channel"), false);
    public final static Type COMMAND = new Type(new IdentifierLiteral("Command"), false);
    public final static Type STRING = new Type(new IdentifierLiteral("String"), true);
    public final static Type NUMBER = new Type(new IdentifierLiteral("Number"), true);
    public final static Type USER = new Type(new IdentifierLiteral("User"), false);
    public final static Type BOOLEAN = new Type(new IdentifierLiteral("Boolean"), false);
    public final static Type DATE = new Type(new IdentifierLiteral("Date"), true);
    public final static Type TIMESPAN = new Type(new IdentifierLiteral("Timespan"), false);
    public final static Type LIST = new Type(new IdentifierLiteral("List"), true);
    public final static Type ANY = new Type(new IdentifierLiteral("Any"), true);
    public final static Type EMPTY_LIST = new Type(new IdentifierLiteral("Leere Liste"), true);
    public final static Type UNKNOWN = new Type(new IdentifierLiteral("UNKNOWN"), false);
    
    private final static Map<String, Type> primitiveCache;
    static {
        primitiveCache = new HashMap<String, Type>();
        primitiveCache.put(CHANNEL.getTypeName().getIdentifier(), CHANNEL);
        primitiveCache.put(COMMAND.getTypeName().getIdentifier(), COMMAND);
        primitiveCache.put(STRING.getTypeName().getIdentifier(), STRING);
        primitiveCache.put(NUMBER.getTypeName().getIdentifier(), NUMBER);
        primitiveCache.put(USER.getTypeName().getIdentifier(), USER);
        primitiveCache.put(BOOLEAN.getTypeName().getIdentifier(), BOOLEAN);
        primitiveCache.put(DATE.getTypeName().getIdentifier(), DATE);
        primitiveCache.put(TIMESPAN.getTypeName().getIdentifier(), TIMESPAN);
        primitiveCache.put(LIST.getTypeName().getIdentifier(), LIST);
        primitiveCache.put(ANY.getTypeName().getIdentifier(), ANY);
        primitiveCache.put(EMPTY_LIST.getTypeName().getIdentifier(), EMPTY_LIST);
        primitiveCache.put(UNKNOWN.getTypeName().getIdentifier(), UNKNOWN);
    }
    
    private IdentifierLiteral typeName;
    private boolean compareable;

    
    
    protected Type(IdentifierLiteral typeName, boolean compareable) {
        this.typeName = typeName;
        this.compareable = compareable;
    }
    
    
    
    public IdentifierLiteral getTypeName() {
        return this.typeName;
    }
    
    
    
    public boolean isCompareable() {
        return this.compareable;
    }
    

    
	public boolean check(Type other) {
	    Type t1 = this;
	    Type t2 = other;
	    return t1 == t2 || t1 == Type.ANY || t2 == Type.ANY;
	}
	
	
	
	public Type unique() {
	    // HACK: This is a serialization hack to maintain unique instances of the
	    //       primitive types.
	    Type t = primitiveCache.get(this.getTypeName().getIdentifier());
	    if (t != null) {
	        return t;
	    }
	    return this;
	}

	
	
	public static void castError(Type from, Type to, Position position) 
	    throws ExecutionException {
	    
        throw new ExecutionException(from + " kann nicht zu " + to + " gecastet werden.",
           position);
	}
	
    
    
    public static void typeError(Type found, Type expected, 
            Position position) throws ParseException {
        
        String msg = "Inkompatible Typen: " + found + " und " + expected + 
                     ". Erwartet: " + expected;
        throw new ParseException(msg, position);
    }
    
    
    
    public static void notCompareable(Type type, 
            Position position) throws ParseException {
        
        throw new ParseException("Typ " + type + " definiert keine Ordnung", 
                position);
    }

    
    
    @Override
    public String toString() {
        return this.getTypeName().toString();
    }
    
    
    
    public Object readResolve() throws ObjectStreamException {
        return this.unique();
    }
}
