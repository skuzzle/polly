package de.skuzzle.polly.sdk.time;

/**
 * Simple utility to convert different time units to milliseconds. 
 * 
 * @author Simon
 * @since 0.9.1
 */
public final class Milliseconds {

    /**
     * Converts from seconds to milliseconds.
     * 
     * @param seconds Time span in seconds.
     * @return The same time span in milliseconds.
     */
    public final static long fromSeconds(long seconds) {
        return seconds * 1000;
    }
    
    
    
    /**
     * Converts from minutes to milliseconds.
     * 
     * @param minutes Time span in minutes.
     * @return The same time span in milliseconds.
     */
    public final static long fromMinutes(long minutes) {
        return fromSeconds(minutes * 60);
    }
    
    
    
    /**
     * Converts from hours to milliseconds.
     * 
     * @param hours Time span in hours.
     * @return The same time span in milliseconds.
     */
    public final static long fromHours(long hours) {
        return fromMinutes(hours * 60);
    }
    
    
    
    /**
     * Converts from days to to milliseconds.
     * 
     * @param days Time span in days.
     * @return The same time span in milliseconds.
     */
    public final static long fromDays(long days) {
        return fromHours(days * 24);
    }
    
    
    
    /**
     * Converts a given time span to milliseconds.
     *  
     * @param hours Hours of the times pan.
     * @param minutes Minutes of the time span.
     * @param seconds Seconds of the time span.
     * @return The same time span in milliseconds.
     */
    public final static long fromTimespan(long hours, long minutes, long seconds) {
        return fromHours(hours) + fromMinutes(minutes) + fromSeconds(seconds);
    }
    
    
    
    /**
     * Converts milliseconds into days with always rounding up the result. that means 
     * a started day will count as a whole one.
     * 
     * @param ms Milliseconds to convert.
     * @return Amount of days.
     */
    public final static long toDays(long ms) {
        double tmp = (double) ms / (1000.0 * 60.0 * 60.0 * 24.0);
        return (long) Math.ceil(tmp);
    }
    
    
    
    private Milliseconds() {}
}
