package de.skuzzle.polly.parsing.ast.declarations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.parsing.ast.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.types.Type;


public class Namespace {
    
    private final static Namespace GLOBAL = new Namespace("~global", null);
    private final static Map<String, Namespace> ROOTS = new HashMap<String, Namespace>();

    /**
     * Gets the toplevel namespace with the given name. If no namespace with that name
     * exists, it is created.
     * 
     * @param name The name of the namespace to retrieve.
     * @return The namespace.
     */
    public final static Namespace forName(String name) {
        if (name.equals(GLOBAL.name)) {
            return GLOBAL;
        }
        Namespace check = ROOTS.get(name);
        if (check == null) {
            check = new Namespace(name);
            ROOTS.put(name, check);
        }
        return check;
    }
    
    

    private final Collection<FunctionDeclaration> functions;
    private final Map<Identifier, VarDeclaration> vars;
    private final Namespace parent;
    private final String name;
    
    
    
    /**
     * Creates a new namespace which parent namespace is the global namespace.
     * 
     * @param name The name of the namespace.
     */
    private Namespace(String name) {
        this(name, GLOBAL);
    }
    
    
    
    /**
     * Creates a new namespace which parent namespace is the given one.
     * 
     * @param name Name of the namespace.
     * @param parent Parent namespace.
     */
    private Namespace(String name, Namespace parent) {
        this.name = name;
        this.parent = parent;
        this.functions = new ArrayList<FunctionDeclaration>();
        this.vars = new HashMap<Identifier, VarDeclaration>();
    }
    
    
    
    /**
     * Creates a new sub namespace of this one and returns it.
     * 
     * @param name Name of the subnamespace.
     * @return The new namespace.
     */
    public Namespace enter(String name) {
        return new Namespace(name, this);
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
     * Declares a new variable in this namespace, overriding any existing var with the
     * same name. If the variable is to be declared public, it will be declared in 
     * the gloab namespace instead of this namespace.
     * 
     * @param decl VarDeclaration to add.
     */
    public void declareVarOverride(VarDeclaration decl) {
        if (decl.isGlobal() && this != GLOBAL) {
            GLOBAL.vars.put(decl.getName(), decl);
        } else {
            this.vars.put(decl.getName(), decl);
        }
    }
    
    
    
    /**
     * Declares a new function in this namespace.
     * 
     * @param decl The function to declare.
     * @throws ASTTraversalException If a function with the same name and signature
     *          already exists in this namespace.
     */
    public void declareFunction(FunctionDeclaration decl) throws ASTTraversalException {
        if (this.tryResolveFunction(decl.getName(), decl.getType()) != null) {
            throw new ASTTraversalException(decl.getPosition(), 
                "Doppelte Deklaration von " + decl.getName());
        }
        if (decl.isGlobal()) {
            GLOBAL.functions.add(decl);
        } else {
            this.functions.add(decl);
        }
    }
    
    
    
    /**
     * Declares a new function in this namespace, overriding any existing function with 
     * the same name and signature.
     * 
     * @param decl The function to declare.
     */
    public void declareFunctionOverride(FunctionDeclaration decl) {
        final Namespace space = decl.isGlobal() ? GLOBAL : this;
        final FunctionDeclaration check = space.tryResolveFunction(
            decl.getName(), decl.getType());
        if (check != null) {
            space.functions.remove(check);
        }
        space.functions.add(decl);
    }
    
    
    
    /**
     * Tries to resolve a function declaration with the given name and signature. If none
     * was found, <code>null</code> is returned.
     * 
     * @param name The name of the function to resolve.
     * @param signature The signature of the function to resolve.
     * @return The resolved declaration or <code>null</code> if non was found.
     */
    public FunctionDeclaration tryResolveFunction(Identifier name, Type signature) {
        for(Namespace space = this; space != null; space = space.parent) {
            for (final FunctionDeclaration decl : space.functions) {
                if (decl.getName().equals(name) && decl.getType().check(signature)) {
                    return decl;
                }
            }
        }
        return null;
    }
    
    
    
    /**
     * Tries to resolve a variable declaration with the given name. If none was found,
     * <code>null</code> is returned.
     * 
     * @param name The name to resolve.
     * @return The resolved declaration or <code>null</code> if none was found.
     */
    public VarDeclaration tryResolveVar(Identifier name) {
        for(Namespace space = this; space != null; space = space.parent) {
            final VarDeclaration vd = space.vars.get(name);
            if (vd != null) {
                return vd;
            }
        }
        return null;
    }
    
    
    
    @Override
    public String toString() {
        String result = this.name;
        for(Namespace space = this.parent; space != null; space = space.parent) {
            result = space.name + "." + result;
        }
        return result;
    }
}