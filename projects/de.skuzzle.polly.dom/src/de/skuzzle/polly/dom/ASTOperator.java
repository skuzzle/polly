package de.skuzzle.polly.dom;


public interface ASTOperator extends ASTName {

    /** Position of the operator relative to its operands */
    public enum OperatorKind {
        /** Operator is a postfix operator thus occurs after its operand(s) */
        POSTFIX,
        /** Operator is a prefix operator thus occurs in front of its operand(s) */
        PREFIX,
        /** Operator is an infix operator thus occurs in between of its operands */
        INFIX,
        /** Operator can not be classified as any more precise type */
        NONE;
    }
    
    
    /**
     * Gets the position of this operator relative to its operand(s).
     * @return The kind of this operator
     */
    public OperatorKind getKind();
    
    /**
     * Sets the kind of this operator.
     * @param kind The new kind.
     */
    public void setKind(OperatorKind kind);
    
    /**
     * Gets the type of this operator as any of this interface's constants.
     * 
     * @return The t ype of the operator.
     */
    public int getType();

    /**
     * Sets the type of this operator.
     * 
     * @param type The new type.
     */
    public void setType(int type);

    @Override
    public ASTOperator getOrigin();
    
    @Override
    public ASTOperator deepOrigin();
    
    @Override
    public ASTOperator copy();
}