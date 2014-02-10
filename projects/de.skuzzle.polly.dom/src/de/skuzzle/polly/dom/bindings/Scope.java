package de.skuzzle.polly.dom.bindings;

import java.util.Collection;

import de.skuzzle.polly.dom.ASTName;


public interface Scope {

    /**
     * Gets the parent scope.
     * 
     * @return The parent scope or <code>null</code> if this is a root scope.
     */
    public Scope getParent();
    
    /**
     * Gets all bindings of this scope.
     * 
     * @return The bindings in this scope.
     */
    public Collection<? extends Binding> getBindings();
    
    /**
     * Finds the binding for the provided name or returns <code>null</code> if it could
     * not be resolved in this scope.
     * 
     * @param name The name to find.
     * @return The binding of that name in this scope.
     */
    public Binding findLocalBinding(ASTName name);
    
    /**
     * Finds the first matching binding for the provided name by searching this scope and
     * all parent scopes.
     * @param name The name to find.
     * @return The binding of that name or <code>null</code> if it could not be found.
     */
    public Binding findBinding(ASTName name);
    
    /**
     * Searches this scope and the parent scopes for binding candidates for the provided
     * name.
     * @param name The name to find bindings for.
     * @return Possible bindings matching that name.
     */
    public Collection<? extends Binding> findBindings(ASTName name);
}