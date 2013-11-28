package de.skuzzle.polly.tools.streams;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BandwidthInputStream extends FilterInputStream {

    
    private final AllocationStrategyProvider provider;

    
    
    public BandwidthInputStream(InputStream stream, int maxBytesPerSecond) {
        this(stream, new IntervalAllocationStrategy(maxBytesPerSecond, 1000));
    }



    public BandwidthInputStream(InputStream stream, AllocationStrategyProvider provider) {
        super(stream);
        if (provider == null) {
            throw new NullPointerException();
        }
        this.provider = provider;
    }
    
    
    
    public double getSpeed() {
        return this.provider.getStrategy().getSpeed();
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
        len = this.provider.getStrategy().allocate(this, len);
        len = super.read(b, off, len);
        return len;
    }



    @Override
    public void close() throws IOException {
        this.provider.getStrategy().close();
        super.close();
    }
}
