package de.skuzzle.polly.dom;

import de.skuzzle.polly.dom.ASTOperator.OperatorKind;

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
    
    /**
     * Creates a new {@link ASTBinaryExpression} from the provided nodes.
     * @param op The operator of the binary expression.
     * @param left The left operand of the expression.
     * @param right The right operand of the expression.
     * @return The created node.
     */
    public ASTBinaryExpression newBinaryExpression(ASTOperator op, ASTExpression left, 
            ASTExpression right);
    
    /**
     * Creates a new {@link ASTBracedExpression} node.
     * @param exp the expression in braces.
     * @return The created node.
     */
    public ASTBracedExpression newBraced(ASTExpression exp);
    
    /**
     * Creates a new {@link ASTOperator}.
     * @param type The operator's type.
     * @param kind The kind of the operator.
     * @return The created node.
     */
    public ASTOperator newOperator(int type, OperatorKind kind);
    
    /**
     * Creates a new {@link ASTUnaryExpression}.
     * @param op The operator of the expression.
     * @param operand The operand.
     * @return The created node.
     */
    public ASTUnaryExpression newUnaryExpression(ASTOperator op, ASTExpression operand);
    
    /**
     * Creates a new {@link ASTStringLiteral}.
     * @param value The string value of the new literal
     * @return The created node.
     */
    public ASTStringLiteral newStringLiteral(String value);
    
    /**
     * Creates a new {@link ASTChannelLiteral}.
     * @param value The String representing the channel.
     * @return The created node.
     */
    public ASTChannelLiteral newChannelLiteral(String value);
    
    /**
     * Creates a new {@link ASTParameter}
     * @param typeName The name of the type of this parameter.
     * @param name The name of the parameter.
     * @return The created node.
     */
    public ASTParameter newParameter(ASTName typeName, ASTName name);
    
    /**
     * Creates a new {@link ASTFunctionExpression} with a body expression.
     * @param body The function's body.
     * @return The created node.
     */
    public ASTFunctionExpression newFunction(ASTExpression body);
}
