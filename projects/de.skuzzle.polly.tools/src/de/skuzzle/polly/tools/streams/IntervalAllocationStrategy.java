package de.skuzzle.polly.tools.streams;


public class IntervalAllocationStrategy implements AllocationStrategy, AllocationStrategyProvider {
    
    protected final Object lock;
    private final int intervalLength;
    private final int maxBytesPerInterval;
    private long lastResetTime;
    private long lastAllocationTime;
    private int lastAllocation;
    private boolean sleep;
    private int bytesLeft;
    private double speed;
    
    
    
    /**
     * Creates a new DefaultAllocationStrategy.
     * 
     * @param pMaxBytesPerInterval The maximum of bytes that can be allocated within the 
     *          provided interval
     * @param pIntervalLength The above mentioned interval in milliseconds.
     */
    public IntervalAllocationStrategy(int pMaxBytesPerInterval, int pIntervalLength) {
        this.lock = new Object();
        this.intervalLength = pIntervalLength;
        this.maxBytesPerInterval = pMaxBytesPerInterval;
        this.bytesLeft = maxBytesPerInterval;
        this.sleep = true;
        this.lastResetTime = System.nanoTime();
        this.lastAllocationTime = System.nanoTime();
    }
    
    
    
    @Override
    public void registerConsumer(Object obj) {
    }
    
    
    
    @Override
    public void consumerFinished(Object obj) {
    }
    
    
    
    @Override
    public AllocationStrategy getStrategy() {
        return this;
    }
    
    
    
    @Override
    public double getSpeed() {
        synchronized (this.lock) {
            return this.speed * 1000000000;
        }
    }

    
    
    @Override
    public int allocate(Object source, int bytes) {
        synchronized (this.lock) {
            
            final long now = System.nanoTime();
            final int totallyAllocated = this.maxBytesPerInterval - this.bytesLeft;
            final long timePassedSinceAllocation = System.nanoTime() - this.lastAllocationTime;
            // reduce amounts of bytes allocated in this run so the currently requested
            // amount of bytes are available by the end of the interval
            if (this.lastAllocation != 0) {
                final double timePassedSinceAllocationMs = timePassedSinceAllocation / 1000000.0;
                final double allocationsDurationMs = (double) this.lastAllocation / (double) timePassedSinceAllocationMs;
                
                final int bytesNew = (int) (((now + this.intervalLength - 1.f + totallyAllocated) * allocationsDurationMs) / (allocationsDurationMs * totallyAllocated * this.maxBytesPerInterval));
                bytes = Math.min(bytesNew, bytes);
            }
            
            final long timePassedSinceReset = System.nanoTime() - this.lastResetTime;
            final long timePassedSinceResetMs = timePassedSinceReset / 1000000;
            final long timeUntilResetMs = Math.max(0, this.intervalLength - timePassedSinceResetMs);
            
            if (timePassedSinceResetMs > this.intervalLength) {
                double bytesPerInterval = (double) (this.maxBytesPerInterval - this.bytesLeft);
                
                this.speed = timePassedSinceReset == 0 ? 0.f : bytesPerInterval / (double) timePassedSinceReset;
                this.bytesLeft = this.maxBytesPerInterval;
                this.lastResetTime = System.nanoTime();
            }
            
            final int allocated = Math.max(0, Math.min(this.bytesLeft, bytes));
            if (this.sleep && allocated == 0) {
                try {
                    // increases the possibility that there are bytes available on the next 
                    // call to allocate
                   Thread.sleep(timeUntilResetMs); 
                } catch (InterruptedException e) {
                    return 0;
                }
            }
            this.bytesLeft -= allocated;
            this.lastAllocation = allocated;
            this.lastAllocationTime = System.nanoTime();
            return allocated;
        }
    }
    
    
    
    @Override
    public void close() {
    }
}
