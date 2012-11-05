package de.skuzzle.polly.parsing.ast;

/**
 * This exception can be thrown by {@link Visitor}s to instantly abort the traversal 
 * process.
 * 
 * @author Simon Taddiken
 *
 */
public class ASTTraversalException extends Exception {

    private static final long serialVersionUID = 1L;

}
