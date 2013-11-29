package de.skuzzle.polly.sdk.eventlistener;


/**
 * This listener is used by the {@link Time} class and will be notified when the
 * day changes.
 * @author Simon
 * @version 0.9.1
 */
public interface DayChangedListener {

    /**
     * Will be called when the day changes.
     * @param currentTime The current time.
     */
    public void dayChanged(long currentTime);
}