package de.skuzzle.polly.core.util;


public class NanoSecondStopWatch implements Stopwatch {

    private long start;
    private long stop;
    
    @Override
    public void start() {
        this.stop = System.nanoTime();
        this.start = System.nanoTime();
    }
    

    @Override
    public long stop() {
        this.stop = System.nanoTime();
        return this.getDifference();
    }

    @Override
    public long getDifference() {
        return this.stop - this.start;
    }

}
