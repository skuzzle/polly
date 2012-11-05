package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.Visitor;

/**
 * This class represents an operator in a {@link BinaryExpression}.
 * 
 * @author Simon Taddiken
 */
public class BinaryOperator extends Node {

    /**
     * Represents an operator type.
     * 
     * @author Simon Taddiken
     */
    public static enum OperatorType {
        ADD("+");
        
        private final String value;
        
        
        private OperatorType(String value) {
            this.value = value;
        }
        
        
        
        /**
         * Gets a String representation of the operator.
         * 
         * @return Operator type's String representation.
         */
        public String getValue() {
            return this.value;
        }
    }

    
    
    private final OperatorType type;


    
    /**
     * Creates a new BinaryOperator.
     * 
     * @param position position of the operator.
     * @param type Operator type.
     */
    public BinaryOperator(Position position, OperatorType type) {
        super(position);
        this.type = type;
    }
    
    
    
    /**
     * Gets the type of this operator.
     * 
     * @return The operator's type.
     */
    public OperatorType getType() {
        return this.type;
    }



    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitBinaryOp(this);
    }
}