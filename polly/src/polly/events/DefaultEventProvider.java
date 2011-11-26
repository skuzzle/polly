package polly.events;

import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.event.EventListenerList;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.exceptions.DisposingException;


/**
 * 
 * @author Simon
 * @version 27.07.2011 ae73250
 */
public class DefaultEventProvider extends AbstractDisposable implements EventProvider {
    
    protected EventListenerList listeners;
    protected ExecutorService dispatchPool;
    
    
    
    public DefaultEventProvider() {
        this(Executors.newFixedThreadPool(1));
    }
    
    
    
    public DefaultEventProvider(ExecutorService dispatcher) {
        this.listeners = new EventListenerList();
        this.dispatchPool = dispatcher;
    }
    
    
    
    @Override
    public <T extends EventListener> List<T> getListeners(Class<T> listenerClass) {
        synchronized (this.listeners) {
            return Arrays.asList(this.listeners.getListeners(listenerClass));
        }
    }

    
    @Override
    public <T extends EventListener> void addListener(Class<T> listenerClass, 
            T listener) {
        synchronized (this.listeners) {
            this.listeners.add(listenerClass, listener);
        }
    }


    
    @Override
    public <T extends EventListener> void removeListener(Class<T> listenerClass, 
            T listener) {
        synchronized (this.listeners) {
            this.listeners.remove(listenerClass, listener);
        }
    }



    @Override
    public void dispatchEvent(Dispatchable<?, ?> d) {
        this.dispatchPool.execute(d);
    }
    
    
    
    @Override
    protected void actualDispose() throws DisposingException {
        this.dispatchPool.shutdownNow();
        try {
            this.dispatchPool.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
