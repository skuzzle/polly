package de.skuzzle.polly.sdk.time;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.skuzzle.polly.sdk.eventlistener.DayChangedListener;

/**
 * Used to obtain and set current polly system time using {@link TimeProvider} 
 * instances.
 * @author Simon
 * @since 0.9.1
 */
public final class Time {

    private static TimeProvider provider = new SystemTimeProvider();
    private final static Collection<DayChangedListener> LISTENERS;
    private final static ScheduledExecutorService EXECUTOR;
    private final static long ONE_DAY = Milliseconds.fromDays(1); 
    private static ScheduledFuture<?> future;
    
    
    static {
        LISTENERS = new ArrayList<DayChangedListener>();
        EXECUTOR = Executors.newSingleThreadScheduledExecutor();
        scheduleEventTimer();
    }
    
    
    
    private synchronized final static void scheduleEventTimer() {
        if (future != null) {
            future.cancel(false);
        }
        final Date midnight = DateUtils.getDayAhead(1);
        long untilMidnight = midnight.getTime() - Time.currentTimeMillis();
        future = EXECUTOR.scheduleAtFixedRate(new Runnable() {
            
            @Override
            public void run() {
                final long now = Time.currentTimeMillis();
                for (final DayChangedListener listener : LISTENERS) {
                    listener.dayChanged(now);
                }
            }
        }, untilMidnight, ONE_DAY, TimeUnit.MILLISECONDS);
    }
    
    
    
    /**
     * Sets a new {@link TimeProvider}.
     * @param timeProvider The provider to set.
     */
    public final static void setProvider(TimeProvider timeProvider) {
        if (!timeProvider.equals(provider)) {
            scheduleEventTimer();
        }
        provider = timeProvider;
    }
    
    
    
    /**
     * Gets the currently set {@link TimeProvider}
     * 
     * @return The provider.
     */
    public static TimeProvider getProvider() {
        return provider;
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
    
    
    
    /**
     * Adds a listener that will be notified when the day changed.
     * 
     * @param listenner The listener to add.
     */
    public final static void addDayChangeListener(DayChangedListener listenner) {
        LISTENERS.add(listenner);
    }
    
    
    
    /**
     * Removes a DayChangedListener
     * 
     * @param listener The listener to remove
     */
    public final static void removeDayChangeListener(DayChangedListener listener) {
        LISTENERS.remove(listener);
    }
    
    
    
    private Time() {}
}
