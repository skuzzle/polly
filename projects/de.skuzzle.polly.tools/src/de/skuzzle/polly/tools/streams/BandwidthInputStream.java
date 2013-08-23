package de.skuzzle.polly.tools.streams;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Queue;

public class BandwidthInputStream extends FilterInputStream {

    
    
    private final AllocationStrategy strategy;
    private final Queue<Allocation> recent;

    
    
    public BandwidthInputStream(InputStream stream, int maxBytesPerSecond) {
        this(stream, new IntervalAllocationStrategy(maxBytesPerSecond, 1000));
    }



    public BandwidthInputStream(InputStream stream, AllocationStrategy strategy) {
        super(stream);
        if (strategy == null) {
            throw new NullPointerException();
        }
        this.strategy = strategy;
        this.recent = new ArrayDeque<>(Allocation.ALLOCATION_HISTORY_SIZE);
    }
    
    
    
    private void add(int bytes) {
        this.recent.add(new Allocation(System.currentTimeMillis(), bytes));
        if (this.recent.size() == Allocation.ALLOCATION_HISTORY_SIZE) {
            this.recent.poll();
        }
    }
    
    
    
    /**
     * Calculates the recent reading speed in bytes per millisecond of this stream. This
     * takes a number of recent reading attempts and the system time at which they 
     * occurred into account.
     * 
     * @return The reading speed in bytes per millisecond.
     */
    public float calculateSpeed() {
        if (this.recent.isEmpty()) {
            return 0.f;
        }
        final long firstTime = this.recent.peek().allocationTime;
        long lastTime = System.currentTimeMillis();
        int bytes = 0;
        for (Allocation a : this.recent) {
            bytes += a.bytes;
            lastTime = a.allocationTime;
        }
        assert lastTime >= firstTime;
        long duration = lastTime - firstTime;
        if (duration == 0) {
            return Float.POSITIVE_INFINITY;
        }
        return (float) bytes / duration;
    }



    @Override
    public int read() throws IOException {
        if (this.strategy.allocate(1) == 1) {
            this.add(1);
            return super.read();
        }
        return 0;
    }



    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }



    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        len = this.strategy.allocate(len);
        len = super.read(b, off, len);
        this.add(len);
        return len;
    }



    @Override
    public void close() throws IOException {
        this.strategy.close();
        super.close();
    }
}
