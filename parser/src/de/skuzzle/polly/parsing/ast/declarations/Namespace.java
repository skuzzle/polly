package de.skuzzle.polly.parsing.ast.declarations;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Braced;
import de.skuzzle.polly.parsing.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.parsing.ast.lang.Cast;
import de.skuzzle.polly.parsing.ast.lang.Operator.OpType;
import de.skuzzle.polly.parsing.ast.lang.functions.FoldLeft;
import de.skuzzle.polly.parsing.ast.lang.operators.BinaryArithmetic;
import de.skuzzle.polly.parsing.ast.lang.operators.BinaryBooleanArithmetic;
import de.skuzzle.polly.parsing.ast.lang.operators.BinaryStringArithmetic;
import de.skuzzle.polly.parsing.ast.lang.operators.Conditional;
import de.skuzzle.polly.parsing.ast.lang.operators.DateArithmetic;
import de.skuzzle.polly.parsing.ast.lang.operators.DateTimespanArithmetic;
import de.skuzzle.polly.parsing.ast.lang.operators.ListIndex;
import de.skuzzle.polly.parsing.ast.lang.operators.RandomListIndex;
import de.skuzzle.polly.parsing.ast.lang.operators.Relational;
import de.skuzzle.polly.parsing.ast.lang.operators.TernaryDotDot;
import de.skuzzle.polly.parsing.ast.lang.operators.TimespanArithmetic;
import de.skuzzle.polly.parsing.ast.lang.operators.UnaryArithmetic;
import de.skuzzle.polly.parsing.ast.lang.operators.UnaryBooleanArithmetic;
import de.skuzzle.polly.parsing.ast.lang.operators.UnaryList;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Unparser;
import de.skuzzle.polly.tools.streams.CopyTool;
import de.skuzzle.polly.tools.strings.StringUtils;



/**
 * <p>This class represents a hierarchical store for variable declarations. Every 
 * namespace has a parent namespace, except the global namespace. Namespaces that are not 
 * created with a parent will automatically have the global namespace as parent. The 
 * global space contains all native operator and constant declarations that can be
 * used.</p>
 * 
 * <p>For each variable name can be stored a list of declarations. Declarations with
 * the same name must have distinct unique types. Resolving of a variable name will first
 * search the current namespace and then all parent namespaces.</p>
 * 
 * <p>Namespaces can be obtained using any of the <code>forName</code> methods. The
 * returned namespace instance will store all declared variables into a file with the
 * name of the namespace.</p> 
 * 
 * @author Simon Taddiken
 */
public class Namespace {
    
    /** 
     * Percentage of word length that will be used as levenshtein threshold in
     * {@link #findSimilar(String, Namespace)}.
     */
    private final static float LEVENSHTEIN_THRESHOLD_PERCENT = 0.6f;
    
    
    public static File declarationFolder;
    public static synchronized void setDeclarationFolder(File declarationFolder) {
        Namespace.declarationFolder = declarationFolder;
    }
    
    
    
    
    
    /**
     * Private namespace extension that will store the namespace's contents to file 
     * whenever a new variable is declared.
     * 
     * @author Simon Taddiken
     */
    private final static class StorableNamespace extends Namespace {

        private String fileName;
        
        
        
        public StorableNamespace(String fileName, Namespace parent) {
            super(parent);
            this.fileName = fileName;
        }
        
        
        
        @Override
        public synchronized void declare(Declaration decl) throws ASTTraversalException {
            super.declare(decl);
            if (!(decl instanceof VarDeclaration)) {
                return;
            }
            final VarDeclaration vd = (VarDeclaration) decl;
            if (vd.isPublic() || vd.isTemp() || vd.isNative()) {
                return;
            }
            
            try {
                this.store();
            } catch (IOException ignore) {}
        }
        
        
        
        @Override
        public int delete(Identifier id) {
            final int i = super.delete(id);
            
            if (i != 0) {
                try {
                    this.store();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return i;
        }
        
        
        
        public void store() throws IOException {
            if (declarationFolder == null) {
                throw new IOException("declaration folder has not been set");
            }
            
            PrintWriter pw = null;
            try {
                pw = new PrintWriter(new File(declarationFolder, this.fileName));
                
                final Unparser up = new Unparser(pw);
                for (final List<Declaration> decls : this.decls.values()) {
                    for (final Declaration decl : decls) {
                        if (!(decl instanceof VarDeclaration)) {
                            continue;
                        }
                        final VarDeclaration vd = (VarDeclaration) decl;
                        new Braced(vd.getExpression()).visit(up);
                        pw.print("->");
                        pw.println(vd.getName().getId());
                    }
                }
            } catch (ASTTraversalException e) {
                throw new RuntimeException(e);
            } finally {
                if (pw != null) {
                    pw.close();
                }
            }
        }
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
     * @see StringUtils
     */
    private final static List<String> findSimilar(String given, Namespace nspace) {
        final int threshold = 
            Math.max(1, (int) Math.round(given.length() * LEVENSHTEIN_THRESHOLD_PERCENT));
        final List<LevenshteinResult> results = 
                new ArrayList<Namespace.LevenshteinResult>();
        
        for(Namespace space = nspace; space != null; space = space.parent) {
            final List<Declaration> decls = space.decls.get(given);
            if (decls == null) {
                continue;
            }
            for (final Declaration decl : decls) {
                
                final String name = decl.getName().getId();
                
                // only take valid identifiers into account (this will skip operator
                // declarations.
                if (!Character.isJavaIdentifierPart(name.charAt(0))) {
                    continue;
                }
                
                final int dist = StringUtils.getLevenshteinDistance(name, given);
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
    private final static Namespace GLOBAL = new StorableNamespace("~global.decl", null);
    static {
        try {
            
            final VarDeclaration pi = new VarDeclaration(Position.NONE, 
                new Identifier(Position.NONE, "pi"), 
                new NumberLiteral(Position.NONE, Math.PI));
            pi.setNative(true);
            final VarDeclaration e = new VarDeclaration(Position.NONE, 
                new Identifier(Position.NONE, "e"), 
                new NumberLiteral(Position.NONE, Math.E));
            e.setNative(true);
            
            GLOBAL.declare(pi);
            GLOBAL.declare(e);
            
            // casting ops
            GLOBAL.declare(new Cast(OpType.STRING, Type.STRING).createDeclaration());
            GLOBAL.declare(new Cast(OpType.NUMBER, Type.NUM).createDeclaration());
            GLOBAL.declare(new Cast(OpType.DATE, Type.DATE).createDeclaration());
            
            // relational ops
            GLOBAL.declare(new Relational(OpType.EQ).createDeclaration());
            GLOBAL.declare(new Relational(OpType.NEQ).createDeclaration());
            GLOBAL.declare(new Relational(OpType.LT).createDeclaration());
            GLOBAL.declare(new Relational(OpType.ELT).createDeclaration());
            GLOBAL.declare(new Relational(OpType.GT).createDeclaration());
            GLOBAL.declare(new Relational(OpType.EGT).createDeclaration());
            
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
            GLOBAL.declare(new BinaryArithmetic(OpType.MIN).createDeclaration());
            GLOBAL.declare(new BinaryArithmetic(OpType.MAX).createDeclaration());
            GLOBAL.declare(new BinaryArithmetic(OpType.XOR).createDeclaration());
            
            // boolean arithmetic
            GLOBAL.declare(new BinaryBooleanArithmetic(OpType.BOOLEAN_AND).createDeclaration());
            GLOBAL.declare(new BinaryBooleanArithmetic(OpType.XOR).createDeclaration());
            GLOBAL.declare(new BinaryBooleanArithmetic(OpType.BOOLEAN_OR).createDeclaration());
            
            // boolean unary
            GLOBAL.declare(new UnaryBooleanArithmetic(OpType.EXCLAMATION).createDeclaration());
            
            // Arithmetic timespan and date binary ops
            GLOBAL.declare(new TimespanArithmetic(OpType.ADD).createDeclaration());
            GLOBAL.declare(new TimespanArithmetic(OpType.SUB).createDeclaration());
            GLOBAL.declare(new DateTimespanArithmetic(OpType.ADD).createDeclaration());
            GLOBAL.declare(new DateTimespanArithmetic(OpType.SUB).createDeclaration());
            GLOBAL.declare(new DateArithmetic(OpType.SUB).createDeclaration());
            
            // String operators
            GLOBAL.declare(new BinaryStringArithmetic(OpType.ADD).createDeclaration());
            
            // Arithmetic unary ops
            GLOBAL.declare(new UnaryArithmetic(OpType.EXCLAMATION).createDeclaration());
            GLOBAL.declare(new UnaryArithmetic(OpType.SUB).createDeclaration());
            GLOBAL.declare(new UnaryArithmetic(OpType.LOG).createDeclaration());
            GLOBAL.declare(new UnaryArithmetic(OpType.LN).createDeclaration());
            GLOBAL.declare(new UnaryArithmetic(OpType.SQRT).createDeclaration());
            GLOBAL.declare(new UnaryArithmetic(OpType.CEIL).createDeclaration());
            GLOBAL.declare(new UnaryArithmetic(OpType.FLOOR).createDeclaration());
            GLOBAL.declare(new UnaryArithmetic(OpType.ROUND).createDeclaration());
            GLOBAL.declare(new UnaryArithmetic(OpType.SIG).createDeclaration());
            GLOBAL.declare(new UnaryArithmetic(OpType.COS).createDeclaration());
            GLOBAL.declare(new UnaryArithmetic(OpType.SIN).createDeclaration());
            GLOBAL.declare(new UnaryArithmetic(OpType.TAN).createDeclaration());
            GLOBAL.declare(new UnaryArithmetic(OpType.ACOS).createDeclaration());
            GLOBAL.declare(new UnaryArithmetic(OpType.ATAN).createDeclaration());
            GLOBAL.declare(new UnaryArithmetic(OpType.ASIN).createDeclaration());
            GLOBAL.declare(new UnaryArithmetic(OpType.ABS).createDeclaration());
            GLOBAL.declare(new UnaryArithmetic(OpType.TO_DEGREES).createDeclaration());
            GLOBAL.declare(new UnaryArithmetic(OpType.TO_RADIANS).createDeclaration());
            GLOBAL.declare(new UnaryArithmetic(OpType.EXP).createDeclaration());
            
            // list unary op
            GLOBAL.declare(new UnaryList(OpType.EXCLAMATION).createDeclaration());
            GLOBAL.declare(new ListIndex(OpType.INDEX).createDeclaration());
            GLOBAL.declare(new RandomListIndex(OpType.QUEST_EXCL).createDeclaration());
            GLOBAL.declare(new RandomListIndex(OpType.QUESTION).createDeclaration());
            
            // DOTDOT
            GLOBAL.declare(new TernaryDotDot().createDeclaration());
            
            // ternary ops
            GLOBAL.declare(new Conditional(OpType.IF).createDeclaration());
            
            
            // FUNCTIONS
            GLOBAL.declare(new FoldLeft().createDeclaration());
            GLOBAL.declare(new de.skuzzle.polly.parsing.ast.lang.functions.Map().createDeclaration());
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
            check = new StorableNamespace(name + ".decl", GLOBAL);
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
    
    

    protected final Map<String, List<Declaration>> decls;
    protected final Namespace parent;
    protected boolean local;
    
    
    
    /**
     * Creates a new namespace which parent namespace is the given one.
     * 
     * @param parent Parent namespace.
     */
    public Namespace(Namespace parent) {
        this.parent = parent;
        this.decls = new HashMap<String, List<Declaration>>();
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
        result.decls.putAll(this.decls);
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
        result.decls.putAll(this.decls);
        return result;
    }
    
    
    
    /**
     * Creates a new sub namespace of this one and returns it.
     * 
     * @param local Whether the new namespace is used for local function parameters. 
     * @return The new namespace.
     */
    public Namespace enter(boolean local) {
        final Namespace ns = new Namespace(this);
        ns.local = local;
        return ns;
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
        if (decl.getType() == Type.UNKNOWN) {
            throw new IllegalStateException(
                "cannot declare variable with unresolved type: " + decl);
        }
        final Map<String, List<Declaration>> decls = decl.isPublic() 
            ? GLOBAL.decls : this.decls;
        
        List<Declaration> d = decls.get(decl.getName().getId());
        if (d == null) {
            d = new ArrayList<Declaration>();
            decls.put(decl.getName().getId(), d);
        }
        
        // check if declaration exists in current namespace
        final Iterator<Declaration> it = d.iterator();
        while (it.hasNext()) {
            final Declaration existing = it.next();
                
            // Existing declaration must be removed, if:
            // * existing is a function and new is a variable
            // * exising is a variable and 
            
            if (Type.unify(existing.getType(), decl.getType(), false, false)) {
                if (!this.local) {
                    if (existing.isNative()) {
                        throw new ASTTraversalException(decl.getPosition(), 
                            "Du kannst keine nativen Deklarationen " +
                            "überschreiben. Deklaration '" + existing.getName() + 
                            "' existiert bereits");
                    }
                    it.remove();
                }
            }
        }
        d.add(decl);
    }
    
    
    
    /**
     * Removes all declarations in this namespace with the given name.
     * 
     * @param id Name of the declaration to delete.
     * @return How many declarations have been removed.
     */
    public int delete(Identifier id) {
        List<Declaration> decls = this.decls.get(id.getId());
        if (decls == null) {
            return 0;
        }
        
        final Iterator<Declaration> it = decls.iterator();
        int i = 0;
        while (it.hasNext()) {
            final Declaration d = it.next();
            
            if (!d.isNative()) {
                it.remove();
                ++i;
            }
        }
        return i;
    }
    
    
    
    /**
     * Resolves the declaration with the given name using only the current declaration
     * level, disregarding all higher levels.
     * 
     * @param name Name of declaration to resolved.
     * @return The resolved declaration.
     * @throws ASTTraversalException If declaration does not exist or multiple 
     *          declarations with that name exists in current level.
     */
    public VarDeclaration resolveHere(ResolvableIdentifier name) 
            throws ASTTraversalException {
        final List<Declaration> decls = this.decls.get(name.getId());
        
        if (decls == null || decls.isEmpty()) {
            throw new ASTTraversalException(name.getPosition(), 
                "Unbekannt: " + name.getId());
        } else if (decls.size() != 1) {
            throw new ASTTraversalException(name.getPosition(), "Ambiguos");
        }
        return (VarDeclaration) decls.get(0);
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
     * @param signature The signature of the variable to resolve.
     * @return The resolved declaration or <code>null</code> if non was found.
     */
    private Declaration tryResolve(ResolvableIdentifier name, Type signature) {
        for(Namespace space = this; space != null; space = space.parent) {
            final List<Declaration> decls = space.decls.get(name.getId());
            if (decls == null) {
                continue;
            }
            for (Declaration decl : decls) {
                
                if (Type.unify(signature, decl.getType(), false, false)) {
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
     * Looks up all possible declarations for the given identifier. Declarations on a 
     * higher level are ignored if a declaration with identical type is found on a lower
     * level.
     * 
     * @param name Variable name to resolve.
     * @return A set of all declarations with matching names.
     * @throws DeclarationException If no variable with matching name was found.
     */
    public Set<Type> lookup(ResolvableIdentifier name) throws DeclarationException {
        final Set<Type> result = new HashSet<Type>();
        for(Namespace space = this; space != null; space = space.parent) {
            final List<Declaration> decls = space.decls.get(name.getId());
            if (decls == null) {
                continue;
            }
            
ignore:     for (Declaration decl : decls) {
                if (decl.getName().equals(name)) {
                    
                    // check if we already found a declaration with the same type on a 
                    // lower declaration level. If so, the current declaration will be
                    // ignored. That will have the effect that declarations on lower 
                    // levels override those on a higher level.
                    for (final Type alreadyFound : result) {
                        if (Type.unify(alreadyFound, decl.getType(), false, false)) {
                            // continue with next declaration and ignore this one
                            continue ignore;
                        }
                    }
                    if (decl.mustCopy()) {
                        decl = CopyTool.copyOf(decl);
                    }
                    result.add(decl.getType());
                    name.addDeclaration(decl);
                }
            }
        }
        
        if (result.isEmpty()) {
            final List<String> similar = findSimilar(name.getId(), this);
            
            throw new DeclarationException(name.getPosition(), 
                "Unbekannte Variable: '" + name + "'", similar);
        }
        return result;
    }
    
    
    
    /**
     * Resolves the declaration belonging to the given name. The declaration to resolve
     * must match the given <code>signature</code>.
     * 
     * @param name Name of declaration to resolve.
     * @param signature Type of declaration to resolve.
     * @return The resolved declaration.
     * @throws ASTTraversalException If declaration with given name and type does not
     *          exist in current namespace hierarchy.
     */
    public VarDeclaration resolveVar(ResolvableIdentifier name, Type signature) 
            throws ASTTraversalException {
        final Declaration check = this.tryResolve(name, signature);
        return (VarDeclaration) check;
    }
    
    
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        int level = 0;
        for(Namespace space = this; space != null; space = space.parent) {
            b.append("Level: ");
            b.append(level++);
            b.append("\n");
            
            for (final List<Declaration> decls : space.decls.values()) {
                final List<Declaration> copy = new ArrayList<Declaration>(decls);
                Collections.sort(copy);
                
                for (final Declaration decl : copy) {
                    b.append("    '");
                    b.append(decl.getName());
                    b.append("'");
                    if (decl instanceof VarDeclaration) {
                        final VarDeclaration vd = (VarDeclaration) decl;
                        b.append(" = ");
                        b.append(Unparser.toString(vd.getExpression()));
                    }
                    b.append(" [Type: ");
                    b.append(decl.getType());
                    b.append("]\n");
                }
            }
        }
        return b.toString();
    }
}