package de.skuzzle.polly.tools.streams;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.skuzzle.polly.tools.concurrent.ThreadFactoryBuilder;


public class IntervalAllocationStrategy implements AllocationStrategy {
    
    
    private int maxBytesPerInterval;
    private final int intervalLength;
    private final ScheduledExecutorService executor;
    private volatile int bytesLeft;
    private long lastReset;
    private boolean sleep;
    
    
    /**
     * Creates a new DefaultAllocationStrategy.
     * 
     * @param maxBytesPerInterval The maximum of bytes that can be allocated within the 
     *          provided interval
     * @param intervalLength The above mentioned interval in milliseconds.
     */
    public IntervalAllocationStrategy(int maxBytesPerInterval, int intervalLength) {
        this.intervalLength = intervalLength;
        this.maxBytesPerInterval = maxBytesPerInterval;
        this.bytesLeft = maxBytesPerInterval;
        this.sleep = false;
        
        this.executor = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder("ALLOCATOR").setDaemon(true));
        this.executor.scheduleAtFixedRate(new Runnable() {
            
            @Override
            public void run() {
                synchronized (IntervalAllocationStrategy.this) {
                    IntervalAllocationStrategy.this.bytesLeft = 
                        IntervalAllocationStrategy.this.maxBytesPerInterval;
                    lastReset = System.currentTimeMillis();
                }
            }
        }, 0, intervalLength, TimeUnit.MILLISECONDS);
    }
    
    
    
    /**
     * Sets the maximum number of bytes that can be allocated within the interval 
     * specified in the constructor.
     * 
     * @param maxBytesPerInterval The new maximum number of bytes per interval.
     */
    public void setMaxBytesPerInterval(int maxBytesPerInterval) {
        synchronized (this) {
            this.maxBytesPerInterval = maxBytesPerInterval;
        }
    }

    
    
    @Override
    public synchronized int allocate(int bytes) {
        final int allocated = Math.max(0, Math.min(this.bytesLeft, bytes));
        if (this.sleep && allocated == 0) {
            final long timePassed = System.currentTimeMillis() - lastReset;
            final long timeLeft = Math.max(0, this.intervalLength - timePassed);
            try {
                // increases the possibility that there are bytes available on the next 
                // call to allocate
               Thread.sleep(timeLeft); 
            } catch (InterruptedException e) {
                return 0;
            }
        }
        this.bytesLeft -= allocated;
        return allocated;
    }
    
    
    
    @Override
    public void close() {
        this.executor.shutdown();
    }
}
