package polly.util;

import de.skuzzle.polly.sdk.time.TimeProvider;


public class TimeProviderStopWatch implements Stopwatch {

    private long start;
    private long stop;
    private TimeProvider timeProvider;
    
    
    
    public TimeProviderStopWatch(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }
    
    
    
    @Override
    public void start() {
        this.stop = timeProvider.currentTimeMillis();
        this.start = timeProvider.currentTimeMillis();
    }
    
    

    @Override
    public long stop() {
        this.stop = this.timeProvider.currentTimeMillis();
        return this.getDifference();
    }

    
    
    @Override
    public long getDifference() {
        return this.stop - this.start;
    }

}
