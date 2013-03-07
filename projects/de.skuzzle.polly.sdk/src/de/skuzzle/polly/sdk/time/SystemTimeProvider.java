package de.skuzzle.polly.sdk.time;


/**
 * Default time provider which always returns the current machines system time.
 * 
 * @author Simon
 * @since 0.7
 */
public final class SystemTimeProvider implements TimeProvider {
    
    @Override
    public final long currentTimeMillis() {
        return System.currentTimeMillis();
    }

}
