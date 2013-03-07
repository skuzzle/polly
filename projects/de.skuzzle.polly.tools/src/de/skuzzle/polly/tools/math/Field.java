package de.skuzzle.polly.tools.math;


/**
 * <p>This class defines an algebraic field over elements from the set {@code T}. That is, 
 * it defines how to multiply and add two elements from {@code T} and specifies neutral 
 * and inverse elements for both operations.</p>
 * 
 * <p>The neutral element of the addition is mostly referred to as the 'zero' element and
 * the neutral element of the multiplication as referred to as the 'one' element.</p>
 * 
 * <p>The additive inverse element to {@code x} is defined as {@code -x} with 
 * {@code x + (-x) == 0} where {@code 0} is the neutral element of addition. Analog,
 * the multiplicative inverse to {@code x} is defined as {@code x^-1} with
 * {@code x * x^-1 == 1} where {@code 1} is the neutral element of multiplication.</p>
 * 
 * <p>Additionally, a field must conform to the rules of associativity and 
 * distributivity. That is, for each {@code x,y,z} from {@code T:} 
 * <ul>
 * <li>{@code x + (y + z) == (x + y) + z}</li>
 * <li>{@code x * (y * z) == (x * y) * z}</li>
 * <li>{@code x * (y + z) == x * y + x * z}</li>
 * </ul>
 * 
 * <p>Implementations of this interface should be commutative in both addition and 
 * multiplication.</p> 
 * 
 * @author Simon
 *
 * @param <T> The set over which this field is defined.
 */
public interface Field<T> {
    
    /**
     * Gets the neutral element of the multiplication. That is the element {@code y} from
     * {@code T} with {@code y * x = x} for each element in {@code T}.
     * 
     * @return The 'one' element of this field.
     */
    public T getMultiplicativeNeutral();
    
    
    
    /**
     * Gets the inverse element {@code x^-1} to any given element {@code x} with 
     * {@code x * x^-1 == 1} where {@code 1} is the element from {@code T} returned by
     * {@link #getMultiplicativeNeutral()}.
     * 
     * @param element The element of which the multiplicative inverse should be 
     *          determined.
     * @return The multiplicative inverse of the given element.
     */
    public T getMultiplicativeInverse(T element);
    
    
    
    /**
     * Multiplies two elements from {@code T} and returns the result. Implementation
     * should be commutative, that is {@code x * y == y * x} for all elements from 
     * {@code T} should be <code>true</code>.
     *  
     * @param left The left element of multiplication.
     * @param right The right element of multiplication.
     * @return The product {@code left * right}.
     */
    public T multiply(T left, T right);
    
    
    
    /**
     * Gets the neutral element of the addition. That is the element {@code y} from
     * {@code T} with {@code y + x = x} for each element in {@code T}.
     * 
     * @return The 'zero' element of this field.
     */
    public abstract T getAdditiveNeutral();
    
    
    
    /**
     * Gets the inverse element {@code -x} to any given element {@code x} with 
     * {@code x + (-x) == 0} where {@code 0} is the element from {@code T} returned by
     * {@link #getAdditiveNeutral()}.
     * 
     * @param element The element of which the additive inverse should be 
     *          determined.
     * @return The additive inverse of the given element.
     */
    public abstract T getAdditiveInverse(T element);
    
    
    
    /**
     * Adds two elements from {@code T} and returns the result. Implementation should
     * be commutative, that is {@code x + y == y + x} for all elements from {@code T} 
     * should be <code>true</code>.
     * 
     * @param left The left element to sum up.
     * @param right The element that is added to the left element
     * @return The sum of left and right.
     */
    public abstract T add(T left, T right);
}
