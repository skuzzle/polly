package polly.util;


public class MillisecondStopwatch implements Stopwatch {

    private long start;
    private long stop;
    
    @Override
    public void start() {
        this.start = System.currentTimeMillis();
        this.stop = System.currentTimeMillis();
    }
    

    @Override
    public long stop() {
        this.stop = System.currentTimeMillis();
        return this.getDifference();
    }

    @Override
    public long getDifference() {
        return this.stop - this.start;
    }

}
