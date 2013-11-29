package de.skuzzle.polly.tools.streams;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class WeightedIntervalAllocationStrategy implements AllocationStrategy {
    
    private final AllocationStrategy intervalStragy;
    private final Map<Object, Integer> priorityMap;
    private int prioritySum = 0;
    
    
    public WeightedIntervalAllocationStrategy(int maxBytesPerInterval, int interval) {
        this.intervalStragy = new IntervalAllocationStrategy(maxBytesPerInterval, interval);
        this.priorityMap = new HashMap<>();
    }
    
    
    
    public void setPriority(Object consumer, int priority) {
        if (priority < 0) {
            throw new IllegalArgumentException("priority < 0: " + priority);
        } else if (!this.priorityMap.containsKey(consumer)) {
            throw new IllegalArgumentException("Unknown consumer: " + consumer);
        }
        final int lastPriority = this.priorityMap.put(consumer, priority);
        this.prioritySum = this.prioritySum - lastPriority + priority;
    }



    @Override
    public void registerConsumer(Object obj) {
        this.intervalStragy.registerConsumer(obj);
        final int priority = 1;
        if (this.priorityMap.put(obj, priority) == null) {
            this.prioritySum += priority;
        }
    }



    @Override
    public void consumerFinished(Object obj) {
        this.intervalStragy.consumerFinished(obj);
        final int priority = this.priorityMap.get(obj);
        this.prioritySum -= priority;
        this.priorityMap.remove(obj);
    }
    
    
    
    @Override
    public double getSpeed() {
        return this.intervalStragy.getSpeed();
    }
    
    
    
    @Override
    public int allocate(Object source, int bytes) {
        final int priority = this.priorityMap.get(source);
        final double p = priority / this.prioritySum;
        bytes = (int) (bytes * p);
        return this.intervalStragy.allocate(source, bytes);
    }
    
    
    
    @Override
    public void close() throws IOException {
        this.intervalStragy.close();
    }
}
