package de.skuzzle.polly.parsing.ast;

/**
 * Interface for classes that can be visited using a {@link Visitor}.
 * 
 * @author Simon Taddiken
 */
public interface Visitable {

    /**
     * Callback method when the AST is iterated with a {@link Visitor}. This method
     * must invoke the matching <code>visitXY</code> method on the passed visitor 
     * according to this nodes type.
     * 
     * @param visitor The visitor to callback.
     * @throws ASTTraversalException May be thrown to abort further traversal of the AST.
     */
    public abstract void visit(Visitor visitor) throws ASTTraversalException;
}