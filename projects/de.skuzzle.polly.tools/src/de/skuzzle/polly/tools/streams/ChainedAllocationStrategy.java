package de.skuzzle.polly.tools.streams;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ChainedAllocationStrategy implements AllocationStrategy {

    private final Map<Object, AllocationStrategy> strategies;
    private AllocationStrategy root;
    
    
    public ChainedAllocationStrategy(AllocationStrategy root) {
        this.root = root;
        this.strategies = new HashMap<>();
    }
    
    
    
    public void setRootStrategy(AllocationStrategy root) {
        this.root = root;
    }
    
    
    @Override
    public void close() throws IOException {
        for (final AllocationStrategy as : this.strategies.values()) {
            as.close();
        }
        this.strategies.clear();
        this.root.close();
    }
    
    

    @Override
    public void registerConsumer(Object obj) {
        this.root.registerConsumer(obj);
    }

    
    
    @Override
    public void consumerFinished(Object obj) {
        this.root.consumerFinished(obj);
    }
    
    

    @Override
    public double getSpeed() {
        return this.root.getSpeed();
    }

    
    
    public void setStrategy(Object consumer, AllocationStrategy strategy) {
        if (strategy == this) {
            throw new IllegalArgumentException("this");
        } else if (strategy == null) {
            throw new NullPointerException();
        }
        final AllocationStrategy old = this.strategies.put(consumer, strategy);
        if (old != null) {
            old.consumerFinished(consumer);
        }
        strategy.registerConsumer(consumer);
    }
    
    
    
    @Override
    public int allocate(Object source, int bytes) {
        final int allocated = this.root.allocate(source, bytes);
        final AllocationStrategy strategy = this.strategies.get(source);
        if (strategy != null) {
            return strategy.allocate(source, allocated);
        }
        return allocated;
    }
}
