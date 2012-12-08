package de.skuzzle.polly.tools;


/**
 * Provides a single static method used to override the {@link #equals(Object)} method
 * when your class implements {@link Equatable}. If so, the implementation of equals()
 * can be reduced to the single following statement:
 * 
 * <pre>
 * &#64;Override
 * public boolean equals(Object obj) {
 *     return EqualsHelper.testEquality(this, obj);
 * }
 * </pre>
 * 
 * @author Simon Taddiken
 * @see Equatable
 */
public final class EqualsHelper {

    /**
     * <p>Tests for equality of both passed objects, obeying the general contract of 
     * Java's {@link #equals(Object)} method. To achieve this, both passed objects must 
     * properly implement the {@link Equatable} interface and <code>o1</code> may never
     * be <code>null</code>.</p>
     * 
     * <p>Two objects <code>o1</code> and <code>o2</code> are considered equal if, and
     * only if:
     * <ul>
     * <li><code>o2 != null</code>, and</li>
     * <li><code>o2 instanceof Equatable</code>, and</li>
     * <li><code>o1.getEquivalenceClass().equals(o2.getEquivalenceClass())</code>, and</li>
     * <li><code>o1.actualEquals(o2)</code></li>
     * </ul>
     * If <code>o1 == o2</code>, the above steps are skipped and <code>true</code> is 
     * returned instantly.</p>
     * 
     * @param o1 The first object. Should always be <code>this</code> when implementing 
     *          <code>equals</code>.
     * @param o2 The object to compare the other object with.
     * @return <code>true</code> iff both objects are considered equals by the above 
     *          steps.
     */
    public final static  boolean testEquality(Equatable o1, Object o2) {
        if (o1 == o2) {
            return true;
        } else if (o2 == null || !(o2 instanceof Equatable)) {
            return false;
        } 
        final Equatable other = (Equatable) o2;
        return o1.getEquivalenceClass().equals(((Equatable) o2).getEquivalenceClass())
            && o1.actualEquals(other);
    }
    
    

    private EqualsHelper() {}    
}
