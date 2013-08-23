package de.skuzzle.polly.tools.streams;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BandwidthOutputStream extends FilterOutputStream {

    private final AllocationStrategy strategy;



    public BandwidthOutputStream(OutputStream stream, int maxBytesPerSecond) {
        this(stream, new IntervalAllocationStrategy(maxBytesPerSecond, 1000));
    }



    public BandwidthOutputStream(OutputStream stream, AllocationStrategy strategy) {
        super(stream);
        if (strategy == null) {
            throw new NullPointerException();
        }
        this.strategy = strategy;
    }



    @Override
    public void write(int b) throws IOException {
        if (this.strategy.allocate(1) == 1) {
            super.write(b);
        }
    }



    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }



    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        len = this.strategy.allocate(len);
        super.write(b, off, len);
    }



    @Override
    public void close() throws IOException {
        this.strategy.close();
        super.close();
    }
}
