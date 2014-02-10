package de.skuzzle.polly.dom;

import java.util.List;

import de.skuzzle.parser.dom.ASTNodeProperty;


public interface ASTFunctionExpression extends ASTExpression, ASTScopeOwner {
    
    /** Property for formal parameters */
    public final static ASTNodeProperty PARAMETER = new ASTNodeProperty("PARAMETER");
    
    /** Property for a function's body */
    public final static ASTNodeProperty BODY = new ASTNodeProperty("BODY");

    
    
    /**
     * Gets the formal parameters of this function. The returned list is read-only and 
     * provides random access to its elements.
     * 
     * @return The formal parameters.
     */
    public List<? extends ASTParameter> getParameters();
    
    /**
     * Adds a new parameter to this function.
     * 
     * @param name The new parameter.
     */
    public void addParameter(ASTParameter param);
    
    /**
     * Gets the body of this function.
     * 
     * @return The body.
     */
    public ASTExpression getBody();
    
    /**
     * Sets the body of this function.
     * 
     * @param body The new body.
     */
    public void setBody(ASTExpression body);
    
    @Override
    public ASTFunctionExpression getOrigin();
    
    @Override
    public ASTFunctionExpression deepOrigin();
    
    @Override
    public ASTFunctionExpression copy();
}