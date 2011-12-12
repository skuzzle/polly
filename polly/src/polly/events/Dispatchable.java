package polly.events;

import java.util.EventListener;
import java.util.List;


public abstract class Dispatchable<T extends EventListener, E> implements Runnable {

    private E event;
    private List<T> listeners;
    
    
    public Dispatchable(List<T> listeners, E event) {
        this.listeners = listeners;
        this.event = event;
    }
    
    public abstract void dispatch(T listener, E event);
    
    
    @Override
    public void run() {
        for (T listener : this.listeners) {
            this.dispatch(listener, this.event);
        }
    }

}
