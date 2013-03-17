package de.skuzzle.polly.tools.events;

import java.util.EventListener;


/**
 * <p>Dispatchables encapsulate a collection of listeners that shall be notified about
 * a certain event. The listeners and event are provided in the constructor, the abstract
 * method {@link #dispatch(EventListener, Event)} specifies on how a single listener
 * is notified about this kind of event.</p>
 * 
 * <p>When being run, all listeners will be notified about the event provided in the
 * constructor. Furthermore, any of the registered listeners may set the 
 * {@link Event#isHandled()} to <code>true</code>, which causes this Dispatchable to
 * stop notifying further listeners. Listeners that implement the 
 * {@link OneTimeEventListener} tagging interface will be removed from its parent 
 * {@link EventProvider} once they were notified about the event they were registered 
 * for.</p>
 * 
 * <p>To run a Dispatchable, call {@link EventProvider#dispatchEvent(Dispatchable)} with 
 * this instance as parameter. The EventProvider will then take care of how to actually 
 * run this Dispatchable.</p>
 * 
 * @author Simon
 *
 * @param <T> The type of the listeners that will be notified.
 * @param <E> The type of the event that the listeners are notified with.
 */
public abstract class Dispatchable<T extends EventListener, E extends Event<?>> 
        implements Runnable {

    protected final E event;
    protected final Listeners<T> listeners;
    
    
    /**
     * Creates a new Dispatchable.
     * 
     * @param listeners Collection of listeners that will be notified when this 
     *          Dispatchable is executed by an {@link EventProvider}.
     * @param event The event with which each listener will be notified. 
     */
    public Dispatchable(Listeners<T> listeners, E event) {
        this.listeners = listeners;
        this.event = event;
    }
    
    
    
    /**
     * Specifies how a single listener is notified about the event.
     * 
     * @param listener The listener to notify.
     * @param event The event.
     */
    public abstract void dispatch(T listener, E event);
    
    
    
    @Override
    public void run() {
        for (T listener : this.listeners) {
            if (this.event.isHandled()) {
                return;
            }
            this.dispatch(listener, this.event);
            if (listener instanceof OneTimeEventListener) {
                this.listeners.removeFromParent(listener);
            }
        }
    }

}
