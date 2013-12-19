package de.skuzzle.polly.tools.io;

import java.io.OutputStream;

import de.skuzzle.polly.tools.events.EventProvider;
import de.skuzzle.polly.tools.events.EventProviders;


public class BandwidthManager {
    
    private final ChainedAllocationStrategy outputStrategy;
    private final EventProvider outputEvents;
    
    
    
    public BandwidthManager() {
        this.outputStrategy = new ChainedAllocationStrategy(null);
        this.outputEvents = EventProviders.newDefaultEventProvider();
    }
    
    

    public BandwidthOutputStream newManagedOutput(OutputStream out, 
            AllocationStrategy subStrategy) {
        final BandwidthOutputStream r = new BandwidthOutputStream(out, this.outputStrategy);
        this.setSubStrategy(r, subStrategy);
        this.outputEvents.addListener(StrategyChangedListener.class, r);
        return r;
    }
    
    
    
    public BandwidthOutputStream newManagedOutput(OutputStream out) {
        final BandwidthOutputStream r = new BandwidthOutputStream(out, this.outputStrategy);
        this.outputEvents.addListener(StrategyChangedListener.class, r);
        return r;
    }
    
    
    
    public void setSubStrategy(BandwidthOutputStream child, AllocationStrategy strategy) {
        this.outputStrategy.setStrategy(child, strategy);
    }
    
    
    
    public void setOutputRootStrategy(AllocationStrategy strategy) {
        if (strategy == null) {
            throw new NullPointerException();
        }
        this.outputStrategy.setRootStrategy(strategy);
        this.fireOutputStrategyChanged(strategy);
    }
    
    
    
    void removeOutputStrategyChangedListener(StrategyChangedListener listener) {
        this.outputEvents.removeListener(StrategyChangedListener.class, listener);
    }
    
    
    
    private void fireOutputStrategyChanged(AllocationStrategy newStrategy) {
        final StrategyChangedEvent e = new StrategyChangedEvent(this, newStrategy);
        this.outputEvents.dispatchEvent(StrategyChangedListener.class, e, 
                StrategyChangedListener.STRATEY_CHANGED);
    }
}
