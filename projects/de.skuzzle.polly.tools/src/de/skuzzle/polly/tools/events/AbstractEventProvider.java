package de.skuzzle.polly.tools.events;

import java.util.Arrays;
import java.util.EventListener;
import java.util.function.BiConsumer;

import javax.swing.event.EventListenerList;


/**
 * Implementation of basic {@link EventProvider} methods.
 * 
 * @author Simon
 */
public abstract class AbstractEventProvider implements EventProvider {
    
    protected final EventListenerList listeners;
    
    
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
    
    
    
    @Override
    public <L extends EventListener, E extends Event<?>> void dispatch(
            Class<L> listenerClass, E event, BiConsumer<L, E> bc, ExceptionCallback ec) {
        if (this.canDispatch()) {
            this.notifyListeners(listenerClass, event, bc, ec);
        }
    }
    
    
    
    /**
     * Notifies all listeners registered for the provided class with the provided event.
     * This method is failure tolerant and will continue notifying listeners even if one
     * of them threw an exception.
     * 
     * @param listenerClass The class of listeners that should be notified.
     * @param event The event to pass to each listener.
     * @param d The method of the listener to call.
     * @return Returns <code>true</code> if all listeners have been notified successfully.
     *          Return <code>false</code> if one listener threw an exception.
     */
    protected <L extends EventListener, E extends Event<?>> boolean notifyListeners(
            Class<L> listenerClass, E event, BiConsumer<L, E> bc, ExceptionCallback ec) {
        
        boolean result = true;
        final Listeners<L> listeners = this.getListeners(listenerClass);
        for (L listener : listeners) {
            try {
                if (event.isHandled()) {
                    return result;
                }
                    
                bc.accept(listener, event);
                if (listener instanceof OneTimeEventListener) {
                    final OneTimeEventListener otl = (OneTimeEventListener) listener;
                    if (otl.workDone()) {
                        this.listeners.remove(listenerClass, listener);
                    }
                }
            } catch (RuntimeException e) {
                result = false;
                try {
                    ec.exception(e);
                } catch (Exception e1) {
                    // where is your god now?
                    e1.printStackTrace();
                }
            }
        }
        return result;
    }
}
