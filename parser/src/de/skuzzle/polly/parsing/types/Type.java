package de.skuzzle.polly.parsing.types;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;


public class Type implements Serializable {
       
    
    private static final long serialVersionUID = 1L;
    
    /*
     * Primitive types
     */
    public final static Type CHANNEL = new Type(new Identifier("Channel"), false);
    public final static Type COMMAND = new Type(new Identifier("Command"), false);
    public final static Type STRING = new Type(new Identifier("String"), true);
    public final static Type NUMBER = new Type(new Identifier("Num"), true);
    public final static Type USER = new Type(new Identifier("User"), false);
    public final static Type BOOLEAN = new Type(new Identifier("Boolean"), false);
    public final static Type DATE = new Type(new Identifier("Date"), true);
    public final static Type TIMESPAN = new Type(new Identifier("Timespan"), false);
    public final static Type LIST = new Type(new Identifier("List"), true);
    public final static Type ANY = new Type(new Identifier("Any"), true);
    public final static Type EMPTY_LIST = new Type(new Identifier("Leere Liste"), true);
    public final static Type HELP = new Type(new Identifier("Help"), false);
    public final static Type UNKNOWN = new Type(new Identifier("UNKNOWN"), false);
    
    private final static Map<String, Type> primitiveCache;

    static {
        primitiveCache = new HashMap<String, Type>();
        primitiveCache.put(CHANNEL.getTypeName().getId(), CHANNEL);
        primitiveCache.put(COMMAND.getTypeName().getId(), COMMAND);
        primitiveCache.put(STRING.getTypeName().getId(), STRING);
        primitiveCache.put(NUMBER.getTypeName().getId(), NUMBER);
        primitiveCache.put(USER.getTypeName().getId(), USER);
        primitiveCache.put(BOOLEAN.getTypeName().getId(), BOOLEAN);
        primitiveCache.put(DATE.getTypeName().getId(), DATE);
        primitiveCache.put(TIMESPAN.getTypeName().getId(), TIMESPAN);
        primitiveCache.put(LIST.getTypeName().getId(), LIST);
        primitiveCache.put(ANY.getTypeName().getId(), ANY);
        primitiveCache.put(HELP.getTypeName().getId(), HELP);
        primitiveCache.put(EMPTY_LIST.getTypeName().getId(), EMPTY_LIST);
        primitiveCache.put(UNKNOWN.getTypeName().getId(), UNKNOWN);
    }
    
    private Identifier typeName;
    private boolean compareable;

    
    
    protected Type(Identifier typeName, boolean compareable) {
        this.typeName = typeName;
        this.compareable = compareable;
    }
    
    
    
    public Identifier getTypeName() {
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
	    Type t = primitiveCache.get(this.getTypeName().getId());
	    if (t != null) {
	        return t;
	    }
	    return this;
	}

	
	
    public static void typeError(Type found, Type expected, 
            Position position) throws ASTTraversalException {
        
        String msg = "Inkompatible Typen: " + found + " und " + expected + 
                     ". Erwartet: " + expected;
        throw new ASTTraversalException(position, msg);
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
