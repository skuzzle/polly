package de.skuzzle.polly.tools.streams;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BandwidthOutputStream extends FilterOutputStream {

    private final AllocationStrategyProvider provider;
    private final SpeedHelper speedHelper;

    public BandwidthOutputStream(OutputStream stream, int maxBytesPerSecond) {
        this(stream, new IntervalAllocationStrategy(maxBytesPerSecond, 1000));
    }



    public BandwidthOutputStream(OutputStream stream, AllocationStrategyProvider provider) {
        super(stream);
        if (provider == null) {
            throw new NullPointerException();
        }
        this.provider = provider;
        this.provider.getStrategy().registerConsumer(this);
        this.speedHelper = new SpeedHelper();
    }

    
    
    public double getSpeed() {
        return this.speedHelper.calculateAvgSpeed();
    }

    

    @Override
    public void write(int b) throws IOException {
        final long start = System.currentTimeMillis();
        if (this.provider.getStrategy().allocate(this, 1) == 1) {
            this.out.write(b);
        }
        final double time = (double) (System.currentTimeMillis() - start);
        this.speedHelper.record(1.0 / time);
    }



    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }



    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        int l = len;
        int allocated = 0;
        final long start = System.currentTimeMillis();
        do {
            l = this.provider.getStrategy().allocate(this, len - allocated);
            this.out.write(b, off + allocated, l);
            allocated += l;
        } while (allocated < len);
        final double time = (double) (System.currentTimeMillis() - start);
        this.speedHelper.record((double) len / time);
    }



    @Override
    public void close() throws IOException {
        this.provider.getStrategy().consumerFinished(this);
        super.close();
    }
}
