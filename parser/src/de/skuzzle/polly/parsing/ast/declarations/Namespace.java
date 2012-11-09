package de.skuzzle.polly.parsing.ast.declarations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.sun.org.apache.xml.internal.utils.NameSpace;

import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
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
    public Namespace(String name, Namespace parent) {
        this.name = name;
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
        final Namespace result = new Namespace(this.name, this.parent.derive());
        result.decls.addAll(this.decls);
        return result;
    }
    
    
    
    /**
     * Creates a new Namespace which contains the same declarations as this one. The
     * new namespace will have the given namespace as parent.
     * 
     * @param parent The parent namespace for the new namespace.
     * @return A new {@link NameSpace}.
     */
    public Namespace derive(Namespace parent) {
        final Namespace result = new Namespace(this.name, parent);
        result.decls.addAll(this.decls);
        return result;
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
     * Declares a new variable in this namespace.
     * 
     * @param decl The function to declare.
     * @throws ASTTraversalException If a function with the same name and signature
     *          already exists in this namespace.
     */
    public void declare(Declaration decl) throws ASTTraversalException {
        final ResolvableIdentifier ri = new ResolvableIdentifier(decl.getName());
        if (this.tryResolve(ri, decl.getType()) != null) {
            throw new ASTTraversalException(decl.getPosition(), 
                "Doppelte Deklaration von " + decl.getName());
        }
        if (decl.isGlobal()) {
            GLOBAL.decls.add(decl);
        } else {
            this.decls.add(decl);
        }
    }
    
    
    
    /**
     * Declares a new function in this namespace, overriding any existing function with 
     * the same name and signature.
     * 
     * @param decl The function to declare.
     */
    public void declareOverride(Declaration decl) {
        final Namespace space = decl.isGlobal() ? GLOBAL : this;
        final ResolvableIdentifier ri = new ResolvableIdentifier(decl.getName());
        final Declaration check = space.tryResolve(ri, decl.getType());
        if (check != null) {
            space.decls.remove(check);
        }
        space.decls.add(decl);
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
            for (final Declaration decl : space.decls) {
                
                if (decl.getName().equals(name) && decl.getType().check(signature)) {
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
        if (check instanceof VarDeclaration) {
            return (VarDeclaration) check;
        }
        throw new ASTTraversalException(name.getPosition(), name.getId() + 
            " ist keine Variable");
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