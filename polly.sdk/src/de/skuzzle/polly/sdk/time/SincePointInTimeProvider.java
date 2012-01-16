package de.skuzzle.polly.sdk.time;


// TODO: SincePointInTimeProvider
public final class SincePointInTimeProvider implements TimeProvider {

    private long point;
    private long start;
    
    
    public SincePointInTimeProvider(long pointIntime) {
        this.point = pointIntime;
        this.start = System.currentTimeMillis();
    }
    
    
    public SincePointInTimeProvider(int day, int month, int year, int hour, int minute, int second) {

    }
    
    public SincePointInTimeProvider(int hour, int minute, int second) {
        
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
