package de.skuzzle.polly.tools.events;

import java.util.Arrays;
import java.util.EventListener;
import java.util.List;

import javax.swing.event.EventListenerList;



public abstract class AbstractEventProvider implements EventProvider {
    
    protected EventListenerList listeners;
    
    
    public AbstractEventProvider() {
        this.listeners = new EventListenerList();
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
}
