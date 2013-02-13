package de.skuzzle.polly.parsing.ast.visitor;


/**
 * Interface for classes that can be visited using a visitor of the supplied type.
 * 
 * @author Simon Taddiken
 * @param <T> Type of the visitor class.
 */
public interface Visitable<T> {

    /**
     * Callback method when the AST is iterated with a {@link Visitor}. This method
     * must invoke the matching <code>visitXY</code> method on the passed visitor 
     * according to this nodes type.
     * 
     * @param visitor The visitor to callback.
     * @throws ASTTraversalException May be thrown to abort further traversal of the AST.
     */
    public abstract void visit(T visitor) throws ASTTraversalException;
}