package de.skuzzle.polly.tools.events;

import java.util.EventListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;




/**
 * 
 * @author Simon
 */
class AsynchronousEventProvider extends AbstractEventProvider {
    
    protected ExecutorService dispatchPool;
    
    
    
    public AsynchronousEventProvider() {
        this(Executors.newFixedThreadPool(1));
    }
    
    
    
    public AsynchronousEventProvider(ExecutorService dispatcher) {
        super();
        this.dispatchPool = dispatcher;
    }
    
    
    
    @Override
    public <L extends EventListener, E extends Event<?>> void dispatchEvent(
            final Class<L> listenerClass, final E event, final Dispatch<L, E> d) {
        if (this.canDispatch()) {
            this.dispatchPool.execute(() -> notifyListeners(listenerClass, event, d));
        }
    }
    
    
    
    @Override
    public boolean canDispatch() {
        return !this.dispatchPool.isShutdown() && !this.dispatchPool.isTerminated();
    }
    


    @Override
    @Deprecated
    public void dispatchEvent(Dispatchable<?, ?> d) {
        if (this.canDispatch()) {
            this.dispatchPool.execute(d);
        }
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
