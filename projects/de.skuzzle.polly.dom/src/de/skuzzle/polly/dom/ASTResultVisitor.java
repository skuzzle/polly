package de.skuzzle.polly.dom;


public class ASTResultVisitor<T> extends ASTVisitor {

    private T value;
    
    public ASTResultVisitor() {
    }
    
    
    public ASTResultVisitor(boolean visitAll) {
        super(visitAll);
    }
    
    
    
    /**
     * Sets the value of this visitor.
     * 
     * @param value The value.
     */
    protected void setValue(T value) {
        this.value = value;
    }
    
    
    
    /**
     * Convenience method to set a value and return a visit result at once. May be used
     * like:
     * <pre>
     * public int visit(ASTName node) {
     *     if (node.getName().equals("foundYou")) {
     *         return setValue(node, PROCESS_ABORT);
     *     }
     *     return PROCESS_CONTINUE;
     * }
     * </pre>
     * @param value The value to set.
     * @param visitResult This value will be returned by this method and must be either 
     *          of {@link ASTVisitor#PROCESS_CONTINUE}, {@link ASTVisitor#PROCESS_SKIP} or
     *          {@link ASTVisitor#PROCESS_ABORT}
     * @return The value of parameter <tt>visitResult</tt>
     */
    protected int setValue(T value, int visitResult) {
        this.setValue(value);
        return visitResult;
    }
    
    
    
    /**
     * Gets the value which this visitor has produces.
     * 
     * @return The value.
     */
    public T getValue() {
        return this.value;
    }
}
