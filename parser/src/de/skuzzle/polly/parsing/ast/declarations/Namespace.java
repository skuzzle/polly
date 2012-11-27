package de.skuzzle.polly.parsing.ast.declarations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.operators.Cast;
import de.skuzzle.polly.parsing.ast.operators.Operator.OpType;
import de.skuzzle.polly.parsing.ast.operators.impl.BinaryArithmetic;
import de.skuzzle.polly.parsing.ast.operators.impl.DateArithmetic;
import de.skuzzle.polly.parsing.ast.operators.impl.DateTimespanArithmetic;
import de.skuzzle.polly.parsing.ast.operators.impl.ListIndex;
import de.skuzzle.polly.parsing.ast.operators.impl.TimespanArithmetic;
import de.skuzzle.polly.parsing.ast.operators.impl.UnaryArithmetic;
import de.skuzzle.polly.parsing.ast.operators.impl.UnaryList;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.types.FunctionType;
import de.skuzzle.polly.parsing.types.Type;
import de.skuzzle.polly.parsing.util.CopyTool;
import de.skuzzle.polly.tools.strings.Levenshtein;


public class Namespace {
    
    /** 
     * Percentage of word length that will be used as levenshtein thresold in
     * {@link #findSimilar(String, Namespace)}.
     */
    private final static float LEVENSHTEIN_THRESHOLD_PERCENT = 0.6f;
    
    
    
    private final static Namespace TYPES = new Namespace(null);
    static {
        try {
        TYPES.declare(new TypeDeclaration(Type.ANY.getTypeName(), Type.ANY));
        TYPES.declare(new TypeDeclaration(Type.BOOLEAN.getTypeName(), Type.BOOLEAN));
        TYPES.declare(new TypeDeclaration(Type.CHANNEL.getTypeName(), Type.CHANNEL));
        TYPES.declare(new TypeDeclaration(Type.STRING.getTypeName(), Type.STRING));
        TYPES.declare(new TypeDeclaration(Type.COMMAND.getTypeName(), Type.COMMAND));
        TYPES.declare(new TypeDeclaration(Type.DATE.getTypeName(), Type.DATE));
        TYPES.declare(new TypeDeclaration(Type.TIMESPAN.getTypeName(), Type.TIMESPAN));
        TYPES.declare(new TypeDeclaration(Type.HELP.getTypeName(), Type.HELP));
        TYPES.declare(new TypeDeclaration(Type.LIST.getTypeName(), Type.LIST));
        TYPES.declare(new TypeDeclaration(Type.USER.getTypeName(), Type.USER));
        TYPES.declare(new TypeDeclaration(Type.NUMBER.getTypeName(), Type.NUMBER));
        } catch (ASTTraversalException e) {
            throw new RuntimeException("impossibru", e);
        }
    }
    
    
    
    /**
     * Resolves a type declaration and stores the {@link TypeDeclaration} instance
     * in the {@link ResolvableIdentifier}'s declaration attribute.
     * 
     * @param name Name of the type to resolve. Refers to {@link Type#getTypeName()}.
     * @return The resolved {@link TypeDeclaration}
     * @throws DeclarationException If no such type exists.
     */
    public final static TypeDeclaration resolveType(ResolvableIdentifier name) 
            throws DeclarationException {
        final Declaration decl = TYPES.tryResolve(name);
        if (decl == null) {
            final List<String> similar = findSimilar(name.getId(), TYPES);
            
            throw new DeclarationException(name.getPosition(), 
                "Unbekannter Typ: '" + name + "'.", similar);
        } else if (!(decl instanceof TypeDeclaration)) {
            throw new IllegalStateException(
                "Namespace 'TYPES' must only contain instances of TypeDeclaration.");
        }
        return (TypeDeclaration) decl;
    }
    
    
    
    /**
     * Helper class that encapsulates the result of a Levenshtein comparison: the distance
     * and the actual found string.
     * @author Simon Taddiken
     */
    private final static class LevenshteinResult implements 
                Comparable<LevenshteinResult> {
        
        private int distance;
        public String string;
        
        public LevenshteinResult(int distance, String string) {
            super();
            this.distance = distance;
            this.string = string;
        }
        
        
        
        @Override
        public int compareTo(LevenshteinResult o) {
            return Integer.compare(this.distance, o.distance);
        }
    }
    
    
    
    /**
     * <p>Tries to find declarations that have a name similar to the given string. All
     * declarations in all parent namespaces, beginning at the given one are searched.
     * All declarations that have a levenshtein distance lower than 
     * {@link #LEVENSHTEIN_THRESHOLD_PERCENT} of the given string's length will be 
     * returned. If no such declaration was found, the resulting list will be empty.</p>
     * 
     * <p>The resulting list will be sorted by the distance. The declaration with the 
     * lowest distance to the given string will be on first place and so on.</p>
     * 
     * @param given The string to compare with all declarations.
     * @param nspace The root namespace to search. All parent namespaces are searched
     *          as well.
     * @return A collection of similar declaration names.
     * @see Levenshtein
     */
    private final static List<String> findSimilar(String given, Namespace nspace) {
        final int threshold = 
            Math.max(1, (int) Math.round(given.length() * LEVENSHTEIN_THRESHOLD_PERCENT));
        final List<LevenshteinResult> results = 
                new ArrayList<Namespace.LevenshteinResult>();
        
        for(Namespace space = nspace; space != null; space = space.parent) {
            for (final Declaration decl : space.decls) {
                
                final String name = decl.getName().getId();
                
                // only take valid identifiers into account (this will skip operator
                // declarations.
                if (!Character.isJavaIdentifierPart(name.charAt(0))) {
                    continue;
                }
                
                final int dist = Levenshtein.getLevenshteinDistance(name, given);
                if (dist <= threshold) {
                    results.add(new LevenshteinResult(dist, decl.getName().getId()));
                }
            }
        }
        Collections.sort(results);
        final List<String> stringResults = new ArrayList<String>(results.size());
        for (final LevenshteinResult lr : results) {
            stringResults.add(lr.string);
        }
        return stringResults;
    }
    
    
    /**
     * This is the root namespace of all user namespaces. It contains all operator
     * declarations as well as public variable and function declarations made by users.
     */
    private final static Namespace GLOBAL = new Namespace(null);
    static {
        try {
            // casting ops
            GLOBAL.declare(new Cast(OpType.STRING, Type.STRING).createDeclaration());
            GLOBAL.declare(new Cast(OpType.NUMBER, Type.NUMBER).createDeclaration());
            
            // Arithmetic binary ops
            GLOBAL.declare(new BinaryArithmetic(OpType.ADD).createDeclaration());
            GLOBAL.declare(new BinaryArithmetic(OpType.SUB).createDeclaration());
            GLOBAL.declare(new BinaryArithmetic(OpType.MUL).createDeclaration());
            GLOBAL.declare(new BinaryArithmetic(OpType.DIV).createDeclaration());
            GLOBAL.declare(new BinaryArithmetic(OpType.INTDIV).createDeclaration());
            GLOBAL.declare(new BinaryArithmetic(OpType.INT_AND).createDeclaration());
            GLOBAL.declare(new BinaryArithmetic(OpType.INT_OR).createDeclaration());
            GLOBAL.declare(new BinaryArithmetic(OpType.MOD).createDeclaration());
            GLOBAL.declare(new BinaryArithmetic(OpType.POWER).createDeclaration());
            GLOBAL.declare(new BinaryArithmetic(OpType.LEFT_SHIFT).createDeclaration());
            GLOBAL.declare(new BinaryArithmetic(OpType.RIGHT_SHIFT).createDeclaration());
            GLOBAL.declare(new BinaryArithmetic(OpType.URIGHT_SHIFT).createDeclaration());
            GLOBAL.declare(new BinaryArithmetic(OpType.RADIX).createDeclaration());
            
            // Arithmetic timespan and date binary ops
            GLOBAL.declare(new TimespanArithmetic(OpType.ADD).createDeclaration());
            GLOBAL.declare(new TimespanArithmetic(OpType.SUB).createDeclaration());
            GLOBAL.declare(new DateTimespanArithmetic(OpType.ADD).createDeclaration());
            GLOBAL.declare(new DateTimespanArithmetic(OpType.SUB).createDeclaration());
            
            DateArithmetic da = new DateArithmetic(OpType.SUB);
            Declaration decl = da.createDeclaration();
            GLOBAL.declare(decl);
            
            // Arithmetic unary ops
            GLOBAL.declare(new UnaryArithmetic(OpType.SUB).createDeclaration());
            
            // list unary op
            GLOBAL.declare(new UnaryList(OpType.EXCLAMATION).createDeclaration());
            GLOBAL.declare(new ListIndex(OpType.INDEX).createDeclaration());
            
        } catch (ASTTraversalException e) {
            e.printStackTrace();
        }
    }
    
    
    
    /** Contains all user namespaces mapped to their user name */
    private final static Map<String, Namespace> ROOTS = new HashMap<String, Namespace>();

    
    
    /**
     * Gets the toplevel namespace with the given name. If no namespace with that name
     * exists, it is created.
     * 
     * @param name The name of the namespace to retrieve.
     * @return The namespace.
     */
    public final static Namespace forName(String name) {
        if (name.equals("~global")) {
            return GLOBAL;
        }
        Namespace check = ROOTS.get(name);
        if (check == null) {
            check = new Namespace();
            ROOTS.put(name, check);
        }
        return check;
    }
    
    
    
    /**
     * Gets the toplevel namespace with the given name. If no namespace with that name
     * exists, it is created.
     * 
     * @param name The name of the namespace to retrieve.
     * @return The namespace.
     */
    public final static Namespace forName(Identifier name) {
        return forName(name.getId());
    }
    
    
    
    /**
     * Checks whether a root namespace with given name exists.
     *  
     * @param name name to check.
     * @return <code>true</code> if a namespace with that name exists.
     */
    public final static boolean exists(String name) {
        return ROOTS.containsKey(name);
    }
    
    
    
    /**
     * Checks whether a root namespace with given name exists.
     *  
     * @param name name to check.
     * @return <code>true</code> if a namespace with that name exists.
     */
    public final static boolean exists(Identifier name) {
        return exists(name.getId());
    }
    
    

    private final Collection<Declaration> decls;
    private final Namespace parent;
    
    
    
    /**
     * Creates a new namespace which parent namespace is the global namespace.
     * 
     * @param name The name of the namespace.
     */
    private Namespace() {
        this(GLOBAL);
    }
    
    
    
    /**
     * Creates a new namespace which parent namespace is the given one.
     * 
     * @param parent Parent namespace.
     */
    public Namespace(Namespace parent) {
        this.parent = parent;
        this.decls = new ArrayList<Declaration>();
    }
    
    
    
    /**
     * Creates a new Namespace that contains the same declarations as this one. Each
     * parent namespace is copied as well. That means any changes made to the derived 
     * namespace are <b>not</b> reflected to the original space. This excludes 
     * modifications made to the stored declarations, as they are the same!
     * 
     * @return A new {@link Namespace}.
     */
    public Namespace derive() {
        if (this.parent == null) {
            return null;
        }
        final Namespace result = new Namespace(this.parent.derive());
        result.decls.addAll(this.decls);
        return result;
    }
    
    
    
    /**
     * Creates a new Namespace which contains the same declarations as this one. The
     * new namespace will have the given namespace as parent.
     * 
     * @param parent The parent namespace for the new namespace.
     * @return A new {@link Namespace}.
     */
    public Namespace derive(Namespace parent) {
        final Namespace result = new Namespace(parent);
        result.decls.addAll(this.decls);
        return result;
    }
    
    
    
    /**
     * Creates a new sub namespace of this one and returns it.
     * 
     * @return The new namespace.
     */
    public Namespace enter() {
        return new Namespace(this);
    }
    
    
    
    /**
     * Gets the parent namespace of this namespace. Returns <code>null</code> for the
     * root namespace.
     * 
     * @return The parent namespace.
     */
    public Namespace getParent() {
        return this.parent;
    }

    
    
    /**
     * Declares a new variable in this namespace.
     * 
     * @param decl The function to declare.
     * @throws ASTTraversalException If a function with the same name and signature
     *          already exists in this namespace.
     */
    public void declare(Declaration decl) throws ASTTraversalException {
        final Collection<Declaration> decls = decl.isGlobal() ? GLOBAL.decls : this.decls;
        // check if declaration exists in current namespace
        for (final Declaration d : decls) {
            if (d.getName().equals(decl.getName()) && d.getType().check(decl.getType())) {
                throw new ASTTraversalException(decl.getPosition(), 
                    "Doppelte Deklaration von " + decl.getName());
            }
        }
        decls.add(decl);
    }
    
    
    
    /**
     * <p>Tries to resolve a declaration with the given name and type. If 
     * none was found, <code>null</code> is returned. This will return the first
     * matching declaration in this or any of the parent namespaces.</p>
     * 
     * <p>If a matching declaration was found, it will, before being returned, stored
     * in the given identifiers declaration attribute.</p>
     * 
     * @param name The name of the variable to resolve.
     * @param signature The signature of the variable to resolve. Use {@link Type#ANY} 
     *          if the signature should be ignored.
     * @return The resolved declaration or <code>null</code> if non was found.
     */
    public Declaration tryResolve(ResolvableIdentifier name, Type signature) {
        for(Namespace space = this; space != null; space = space.parent) {
            for (Declaration decl : space.decls) {
                
                if (decl.getName().equals(name) && decl.getType().check(signature)) {
                    if (decl.mustCopy()) {
                        decl = CopyTool.copyOf(decl);
                    }
                    name.setDeclaration(decl);
                    return decl;
                }
            }
        }
        return null;
    }
    
    
    
    /**
     * Tries to resolve a declaration, disregarding the type of the declaration. This
     * will resolve the first matching declaration in this or any parent namespace. This 
     * method is equivalent to calling <code>tryResolve(name, Type.ANY)</code>.
     * 
     * @param name The name of the function to resolve.
     * @return The resolved declaration or <code>null</code> if non was found.
     * @see #tryResolve(ResolvableIdentifier, Type)
     */
    public Declaration tryResolve(ResolvableIdentifier name) {
        return this.tryResolve(name, Type.ANY);
    }
    
    
    
    public VarDeclaration resolveVar(ResolvableIdentifier name, Type signature) 
            throws ASTTraversalException {
        final Declaration check = this.tryResolve(name, signature);
        if (check == null && signature instanceof FunctionType) {
            final FunctionType ft = (FunctionType) signature;
            
            throw new ASTTraversalException(name.getPosition(), 
                "Keine Überladung der Funktion '" + name + "' mit der Signatur " + 
                    ft.toString(false) + " gefunden");
        } else if (check == null) {
            final List<String> similar = findSimilar(name.getId(), this);
            
            throw new DeclarationException(name.getPosition(), 
                "Unbekannte Variable: '" + name + "'.", similar);
        } else if (check instanceof VarDeclaration) {
            return (VarDeclaration) check;
        }
        throw new ASTTraversalException(name.getPosition(), name.getId() + 
            " ist keine Variable");
    }
}