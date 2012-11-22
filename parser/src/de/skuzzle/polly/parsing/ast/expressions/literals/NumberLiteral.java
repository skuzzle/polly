package de.skuzzle.polly.parsing.ast.expressions.literals;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.types.Type;


public class NumberLiteral extends Literal {

    private final double value;
    private int radix;
    
    
    
    public NumberLiteral(Position position, double value) {
        super(position, Type.NUMBER);
        this.value = value;
        this.radix = 10;
    }
    
    
    
    public int getRadix() {
        return this.radix;
    }
    
    
    
    public void setRadix(int radix) {
        this.radix = radix;
    }
    
    
    
    /**
     * Gets the value of this literal.
     * 
     * @return The literal's value.
     */
    public double getValue() {
        return this.value;
    }
    
    
    
    /**
     * Asserts that this literal's value is an integer number. If not, an 
     * {@link ASTTraversalException} will be thrown. The exception uses this literal's
     * position.
     * 
     * @return This literal's value as integer.
     * @throws ASTTraversalException If {@link #getValue()} returns 0.
     */
    public int isInteger() throws ASTTraversalException {
        return this.isInteger(this.getPosition());
    }
    
    
    
    /**
     * Asserts that this literal's value is an integer number. If not, an 
     * {@link ASTTraversalException} will be thrown.
     * 
     * @param pos Position that will be reported in the thrown exception.
     * @return This literal's value as integer.
     * @throws ASTTraversalException If {@link #getValue()} returns 0.
     */
    public int isInteger(Position pos) throws ASTTraversalException {
        if (Math.round(this.getValue()) != this.getValue()) {
            throw new ASTTraversalException(pos, "'" + this.getValue() + 
                "' ist keine Ganzzahl"); 
        }
        return (int) Math.round(this.getValue());
    }
    
    
    
    /**
     * Asserts that this literal's value is non zero and integer. If not, an 
     * {@link ASTTraversalException} will be thrown. The exception uses this literal's
     * position.
     * @return This literal's value as returned by {@link #getValue()}.
     * @throws ASTTraversalException If {@link #getValue()} returns 0.
     */
    public int nonZeroInteger() throws ASTTraversalException {
        this.nonZero();
        return this.isInteger();
    }
    
    
    
    /**
     * Asserts that this literal's value is non zero and integer. If not, 
     * an {@link ASTTraversalException} will be thrown.
     * 
     * @param pos Position that will be reported in the thrown exception.
     * @return This literal's value as returned by {@link #getValue()}.
     * @throws ASTTraversalException If {@link #getValue()} returns 0 or the value
     *          is no integer.
     */
    public int nonZeroInteger(Position pos) throws ASTTraversalException {
        this.nonZero(pos);
        return this.isInteger(pos);
    }
    
    
    
    /**
     * Asserts that this literal's value is non zero. If it is zero, an 
     * {@link ASTTraversalException} exception is thrown. The exception uses the position
     * of this literal.
     * @return This literal's value as returned by {@link #getValue()}.
     * @throws ASTTraversalException If {@link #getValue()} returns 0.
     */
    public double nonZero() throws ASTTraversalException {
        return this.nonZero(this.getPosition());
    }
    
    
    
    /**
     * Asserts that this literal's value is non zero. If it is zero, an 
     * {@link ASTTraversalException} exception is thrown. 
     * 
     * @param pos Position that will be reported in the thrown exception.
     * @return This literal's value as returned by {@link #getValue()}.
     * @throws ASTTraversalException If {@link #getValue()} returns 0.
     */
    public double nonZero(Position pos) throws ASTTraversalException {
        if (this.getValue() == 0.0) {
            throw new ASTTraversalException(pos, "Division durch 0");
        }
        
        return this.getValue();
    }
    
    
    
    @Override
    public Literal castTo(Type type) throws ASTTraversalException {
        return null;
    }

    
    
    @Override
    public String format(LiteralFormatter formatter) {
        return formatter.formatNumberLiteral(this);
    }

    
    
    public int compareTo(Literal o) {
        if (o instanceof NumberLiteral) {
            return Double.compare(this.value, ((NumberLiteral) o).value);
        }
        return super.compareTo(o);
    };
    
    
    
    @Override
    public String toString() {
        return Double.toString(this.value);
    };
}
