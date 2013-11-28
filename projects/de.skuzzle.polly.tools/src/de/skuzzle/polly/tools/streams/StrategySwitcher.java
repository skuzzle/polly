package de.skuzzle.polly.tools.streams;


public class StrategySwitcher implements AllocationStrategyProvider {
    
    private AllocationStrategy strategy;
    
    
    
    public StrategySwitcher(AllocationStrategy strategy) {
        this.setStrategy(strategy);
    }
    
    
    
    public synchronized void setStrategy(AllocationStrategy strategy) {
        if (strategy == null) {
            throw new NullPointerException();
        }
        this.strategy = strategy;
    }
    
    
    
    @Override
    public AllocationStrategy getStrategy() {
        return this.strategy;
    }
}
