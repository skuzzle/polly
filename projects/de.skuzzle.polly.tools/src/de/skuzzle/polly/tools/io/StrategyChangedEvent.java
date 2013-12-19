package de.skuzzle.polly.tools.io;

import de.skuzzle.polly.tools.events.Event;


public class StrategyChangedEvent extends Event<BandwidthManager> {

    private final AllocationStrategy newStrategy;
    
    public StrategyChangedEvent(BandwidthManager source, AllocationStrategy newStrategy) {
        super(source);
        if (newStrategy == null) {
            throw new NullPointerException();
        }
        this.newStrategy = newStrategy;
    }

    
    
    public AllocationStrategy getNewStrategy() {
        return this.newStrategy;
    }
}
