package de.skuzzle.polly.sdk.time;

import java.util.Date;

/**
 * Used to obtain and set current polly system time using {@link TimeProvider} 
 * instances.
 * @author Simon
 * @since 0.9.1
 */
public final class Time {

    private static TimeProvider provider = new SystemTimeProvider();
    
    
    /**
     * Sets a new {@link TimeProvider}.
     * @param timeProvider The provider to set.
     */
    public final static void setProvider(TimeProvider timeProvider) {
        provider = timeProvider;
    }
    
    
    
    /**
     * Gets the current polly system time calculated by the time provider set with
     * {@link #setProvider(TimeProvider)}. By default, this method returns the same time
     * as {@link System#currentTimeMillis()}.
     * @return The current time as millisecond timestamp.
     */
    public final static long currentTimeMillis() {
        return provider.currentTimeMillis();
    }
    
    
    
    /**
     * Returns the current polly system time as a {@link Date} instance.
     * 
     * @return The current time.
     */
    public final static Date currentTime() {
        return new Date(currentTimeMillis());
    }
    
    private Time() {}
}
