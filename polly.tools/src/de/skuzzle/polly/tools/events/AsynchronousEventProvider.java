package de.skuzzle.polly.tools.events;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;




/**
 * 
 * @author Simon
 * @version 27.07.2011 ae73250
 */
public class AsynchronousEventProvider extends AbstractEventProvider {
    
    protected ExecutorService dispatchPool;
    
    
    
    public AsynchronousEventProvider() {
        this(Executors.newFixedThreadPool(1));
    }
    
    
    
    public AsynchronousEventProvider(ExecutorService dispatcher) {
        super();
        this.dispatchPool = dispatcher;
    }
    


    @Override
    public void dispatchEvent(Dispatchable<?, ?> d) {
        this.dispatchPool.execute(d);
    }
    
    
    
    @Override
    public void dispose() {
        this.dispatchPool.shutdownNow();
        try {
            this.dispatchPool.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
