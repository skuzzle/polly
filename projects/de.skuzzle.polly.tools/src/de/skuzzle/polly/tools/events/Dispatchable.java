package de.skuzzle.polly.tools.events;

import java.util.EventListener;


public abstract class Dispatchable<T extends EventListener, E> implements Runnable {

    private E event;
    private Listeners<T> listeners;
    
    
    public Dispatchable(Listeners<T> listeners, E event) {
        this.listeners = listeners;
        this.event = event;
    }
    
    
    
    public abstract void dispatch(T listener, E event);
    
    
    
    @Override
    public void run() {
        for (final T listener : this.listeners) {
            this.dispatch(listener, this.event);
            if ( listener instanceof OneTimeEventListener) {
                this.listeners.removeFromParent(listener);
            }
        }
    }

}
