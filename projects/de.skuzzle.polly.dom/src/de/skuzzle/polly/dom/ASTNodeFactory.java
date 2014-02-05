package de.skuzzle.polly.dom;

/**
 * Factory for creating new AST nodes. Any created node will have no location and no
 * parent assigned and will not be frozen.
 * 
 * @author Simon Taddiken
 */
public interface ASTNodeFactory {

    /**
     * Creates a new ASTName node.
     * 
     * @param name Name of the node. Must not be <code>null</code>
     * @return The created node
     */
    public ASTName newName(String name);
    
    /**
     * Creates a new {@link ASTQualifiedName} from the provided names. The provided 
     * array must not be <code>null</code> or empty.
     * 
     * @param names The names that make up the qualified name.
     * @return The created node.
     */
    public ASTQualifiedName newQualifiedName(ASTName...names);
    
    /**
     * Creates a new {@link ASTQualifiedName} from the provided names. The provided 
     * array must not be <code>null</code> or empty.
     * 
     * @param names The names that make up the qualified name.
     * @return The created node.
     */
    public ASTQualifiedName newQualifiedName(String...names);
    
    /**
     * Creates a new empty product expression.
     * @return The created node.
     */
    public ASTProductExpression newProduct();
    
    /**
     * Creates a new {@link ASTProductExpression} from the provided expressions.
     * @param expressions Expressions of the product node.
     * @return The created node.
     */
    public ASTProductExpression newProduct(ASTExpression... expressions);
    
    /**
     * Creates a new {@link ASTIdExpression} with the provided name.
     * @param name The name for the id expression
     * @return The created node.
     */
    public ASTIdExpression newIdExpression(ASTName name);
    
    /**
     * Creates a new {@link ASTCallExpression}.
     * @param lhs The left hand side of the call.
     * @param rhs The actual parameters of the call.
     * @return The created node
     */
    public ASTCallExpression newCall(ASTExpression lhs, ASTProductExpression rhs);
}
