package de.skuzzle.polly.tools.events;

import java.util.Arrays;
import java.util.EventListener;

import javax.swing.event.EventListenerList;


/**
 * Implementation of basic {@link EventProvider} methods.
 * 
 * @author Simon
 */
public abstract class AbstractEventProvider implements EventProvider {
    
    protected EventListenerList listeners;
    
    
    public AbstractEventProvider() {
        this.listeners = new EventListenerList();
    }
    

    
    @Override
    public <T extends EventListener> Listeners<T> getListeners(Class<T> listenerClass) {
        synchronized (this.listeners) {
            return new Listeners<T>(
                Arrays.asList(this.listeners.getListeners(listenerClass)), 
                listenerClass, this);
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
}
