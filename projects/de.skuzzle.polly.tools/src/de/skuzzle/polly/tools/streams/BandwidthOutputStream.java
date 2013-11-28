package de.skuzzle.polly.tools.streams;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BandwidthOutputStream extends FilterOutputStream {

    private final AllocationStrategyProvider provider;
    

    public BandwidthOutputStream(OutputStream stream, int maxBytesPerSecond) {
        this(stream, new IntervalAllocationStrategy(maxBytesPerSecond, 1000));
    }



    public BandwidthOutputStream(OutputStream stream, AllocationStrategyProvider provider) {
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
    public void write(int b) throws IOException {
        if (this.provider.getStrategy().allocate(this, 1) == 1) {
            this.out.write(b);
        }
    }



    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }



    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        int l = len;
        int allocated = 0;
        do {
            l = this.provider.getStrategy().allocate(this, len - allocated);
            this.out.write(b, off + allocated, l);
            allocated += l;
        } while (allocated < len);
    }



    @Override
    public void close() throws IOException {
        this.provider.getStrategy().close();
        super.close();
    }
}
