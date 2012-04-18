package de.skuzzle.polly.sdk.time;


/**
 * <p>Using this {@link TimeProvider} implementation you can make the time go on faster or
 * slower depending on the speed you specify in the constructor.</p>
 * 
 * <p>The current time will be calculated by calculating the time elapsed since the 
 * construction of this class, multiplying this difference with the speed and then adding
 * the result back to the construction time. The time elapsed is calculated using a 
 * relative TimeProvider specified by the constructor.</p>
 *  
 * @author Simon
 * @since 0.9.1
 */
public class FastTimeProvider implements TimeProvider {

    private TimeProvider relative;
    private double speed;
    private long start;
    
    
    /**
     * Creates a new FastTimeProvider relative to the current system time with the given 
     * speed.
     * 
     * @param speed The speed of this TimeProvider.
     */
    public FastTimeProvider(double speed) {
        this(new SystemTimeProvider(), speed);
    }
    
    
    
    /**
     * Creates a new FastTimeProvider relative to the given TimeProvider with the given
     * speed.
     * 
     * @param relative The relative TimeProvider used to calculate the elapsed time since
     *          construction of this class.
     * @param speed The speed of this TimeProvider.
     */
    public FastTimeProvider(TimeProvider relative, double speed) {
        this.relative = relative;
        this.speed = speed;
        this.start = relative.currentTimeMillis();
    }
    
    
    
    @Override
    public long currentTimeMillis() {
        long elapsed = this.relative.currentTimeMillis() - this.start;
        return this.start + Math.round(elapsed * this.speed);
    }
}
