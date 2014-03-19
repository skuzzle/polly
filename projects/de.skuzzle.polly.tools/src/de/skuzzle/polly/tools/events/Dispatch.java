package de.skuzzle.polly.tools.events;

import java.util.EventListener;
import java.util.function.BiConsumer;

/**
 * Functional interface to notify a listener about an event.
 * 
 * @author Simon Taddiken
 * @param <L> Type of the listener to notify.
 * @param <E> Type of the event.
 */
public interface Dispatch<L extends EventListener, E extends Event<?>> 
        extends BiConsumer<L, E> {

    @Override
    public default void accept(L listener, E event) {
        this.dispatch(listener, event);
    }
    
    /**
     * Delegates the provided element to the listener.
     * 
     * @param listener The listener to notify.
     * @param event The occurred event.
     */
    public void dispatch(L listener, E event);
}
