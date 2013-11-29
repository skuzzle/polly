package de.skuzzle.polly.tools.streams;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BandwidthInputStream extends FilterInputStream {

    
    private final AllocationStrategy strategy;

    
    
    public BandwidthInputStream(InputStream stream, int maxBytesPerSecond) {
        this(stream, new IntervalAllocationStrategy(maxBytesPerSecond, 1000));
    }



    public BandwidthInputStream(InputStream stream, AllocationStrategy strategy) {
        super(stream);
        if (strategy == null) {
            throw new NullPointerException();
        }
        this.strategy = strategy;
        this.strategy.registerConsumer(this);
    }
    
    
    
    public double getSpeed() {
        return this.strategy.getSpeed();
    }



    @Override
    public int read() throws IOException {
        if (this.strategy.allocate(this, 1) == 1) {
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
        len = this.strategy.allocate(this, len);
        len = super.read(b, off, len);
        return len;
    }



    @Override
    public void close() throws IOException {
        this.strategy.consumerFinished(this);
        super.close();
    }
}
