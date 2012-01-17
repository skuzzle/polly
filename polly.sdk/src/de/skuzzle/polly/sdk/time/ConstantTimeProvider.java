package de.skuzzle.polly.sdk.time;


/**
 * This simple {@link TimeProvider} implementation always returns the same time which
 * has been set in the constructor. The time returned can be modified using 
 * {@link #setTime(long)}.
 * 
 * @author Simon
 * @since 0.7
 */
public final class ConstantTimeProvider implements TimeProvider {

    private long time;
    
    
    /**
     * Creates a new ConstantTimeProvider with the given time.
     * 
     * @param time The time which will be returned by {@link #currentTimeMillis()}.
     */
    public ConstantTimeProvider(long time) {
        this.time = time;
    }
    
    
    
    /**
     * Sets the time which will be returned by {@link #currentTimeMillis()}
     * 
     * @param time The new constant time.
     */
    public void setTime(long time) {
        this.time = time;
    }
    
    
    
    @Override
    public long currentTimeMillis() {
        return this.time;
    }
}
