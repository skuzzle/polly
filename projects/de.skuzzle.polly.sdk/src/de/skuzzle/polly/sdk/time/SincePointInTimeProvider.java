package de.skuzzle.polly.sdk.time;

import java.util.Calendar;


/**
 * This class provides the time relative to a point in the past, running as fast as the
 * normal system clock.
 * 
 * @author Simon
 * @since 0.8
 */
public final class SincePointInTimeProvider implements TimeProvider {

    private long point;
    private long start;
    
    
    
    /**
     * Creates a new {@link SincePointInTimeProvider} from a timestamp.
     * 
     * @param pointIntime The timestamp to which this {@link TimeProvider}s time will
     *          be relative.
     */
    public SincePointInTimeProvider(long pointIntime) {
        this.point = pointIntime;
        this.start = System.currentTimeMillis();
    }
    
    
    
    /**
     * Creates a new {@link SincePointInTimeProvider} from a given date.
     * 
     * @param day The day.
     * @param month The month.
     * @param year The year.
     * @param hour The hour of the day (0-24)
     * @param minute The minute.
     * @param second The second.
     */
    public SincePointInTimeProvider(int day, int month, int year, int hour, int minute, 
            int second) {

        Calendar c = Calendar.getInstance();
        c.set(year, month, day, hour, minute, second);
        this.point = c.getTimeInMillis();
        this.start = System.currentTimeMillis();
    }
    
    
    
    /**
     * Creates a new {@link SincePointInTimeProvider} from a given time. Year, month and
     * day will refer to the current data as provided by the machines system time.
     * 
     * @param hour The hour of the day (0-24)
     * @param minute The minute.
     * @param second The second.
     */
    public SincePointInTimeProvider(int hour, int minute, int second) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        this.point = c.getTimeInMillis();
        this.start = System.currentTimeMillis();
    }
    
    
    
    /**
     * {@inheritDoc}
     * 
     * @return The current time relative to the point in time of this class.
     */
    @Override
    public long currentTimeMillis() {
        return this.point + (System.currentTimeMillis() - start);
    }

}
