package de.skuzzle.polly.dom;


public interface ASTCallable {

    /**
     * Converts this node into a {@link ASTCallExpression}.
     * @return The created call expression.
     */
    public ASTCallExpression asCall();
}