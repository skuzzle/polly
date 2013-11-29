package de.skuzzle.polly.tools.streams;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BandwidthOutputStream extends FilterOutputStream 
        implements StrategyChangedListener {

    private AllocationStrategy strategy;
    private final SpeedHelper speedHelper;
    private BandwidthManager manager;
    

    public BandwidthOutputStream(OutputStream stream, int maxBytesPerSecond) {
        this(stream, new IntervalAllocationStrategy(maxBytesPerSecond, 1000));
    }



    public BandwidthOutputStream(OutputStream stream, AllocationStrategy strategy) {
        super(stream);
        if (strategy == null) {
            throw new NullPointerException();
        }
        this.strategy = strategy;
        this.speedHelper = new SpeedHelper();
    }
    
    
    
    BandwidthOutputStream(OutputStream stream, AllocationStrategy strategy, 
            BandwidthManager manager) {
        this(stream, strategy);
        this.manager = manager;
    }
    
    
    @Override
    public void strategyChanged(StrategyChangedEvent e) {
        this.strategy.consumerFinished(this);
        this.strategy = e.getNewStrategy();
        this.strategy.registerConsumer(this);
    }

    
    
    public double getSpeed() {
        return this.speedHelper.calculateAvgSpeed();
    }

    

    @Override
    public void write(int b) throws IOException {
        final long start = System.currentTimeMillis();
        if (this.strategy.allocate(this, 1) == 1) {
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
            l = this.strategy.allocate(this, len - allocated);
            this.out.write(b, off + allocated, l);
            allocated += l;
        } while (allocated < len);
        final double time = (double) (System.currentTimeMillis() - start);
        this.speedHelper.record((double) len / time);
    }



    @Override
    public void close() throws IOException {
        this.strategy.consumerFinished(this);
        if (this.manager != null) {
            this.manager.removeOutputStrategyChangedListener(this);
        }
        super.close();
    }
}
