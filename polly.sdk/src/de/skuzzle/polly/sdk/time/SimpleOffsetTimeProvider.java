package de.skuzzle.polly.sdk.time;


/**
 * This {@link TimeProvider} implementation returns a time relative to the time
 * computed by another TimeProvider.
 * 
 * @author Simon
 * @since 0.7
 */
public final class SimpleOffsetTimeProvider implements TimeProvider {

    private int offset;
    private TimeProvider baseTime;
    
    /**
     * Creates a new {@link SimpleOffsetTimeProvider}. 
     * 
     * @param baseTime The {@link TimeProvider} which computes the base 
     *          time for the offset. 
     * @param offset The offset which is added to the base time.
     */
    public SimpleOffsetTimeProvider(TimeProvider baseTime, int offset) {
        this.baseTime = baseTime;
        this.offset = offset;
    }
    
    
    /**
     * Creates a new {@link SimpleOffsetTimeProvider} relative to the current system
     * time.
     * 
     * @param offset The offset which is added to the base time.
     */
    public SimpleOffsetTimeProvider(int offset) {
        this(new SystemTimeProvider(), offset);
    }
    
    
    /**
     * {@inheritDoc}
     * 
     * @return The time of this class' basetime provider + the offset.
     */
    @Override
    public long currentTimeMillis() {
        return this.baseTime.currentTimeMillis() + this.offset;
    }
    
}