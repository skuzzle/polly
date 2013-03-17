package de.skuzzle.polly.tools.events;

import java.util.concurrent.ExecutorService;

/**
 * Factory class to create several types of {@link EventProvider} instances.
 * 
 * @author Simon
 */
public final class EventProviders {

    /**
     * Creates a new {@link EventProvider} which fires events sequentially in the thread
     * which calls {@link EventProvider#dispatchEvent(Dispatchable)}.
     * 
     * @return A new EventProvider instance.
     */
    public static EventProvider newDefaultEventProvider() {
        return new SynchronousEventProvider();
    }
    
    
    
    /**
     * Creates a new {@link EventProvider} which fires each event in a different thread.
     * By default, the returned {@link EventProvider} uses a cached executor service.
     * 
     * @return A new EventProvider instance.
     */
    public static EventProvider newAsynchronousEventProvider() {
        return new AsynchronousEventProvider();
    }
    
    
    
    /**
     * Creates a new {@link EventProvider} which fires each event in a different thread.
     * The created provider will use the given {@link ExecutorService} to fire the events
     * asynchronously.
     * 
     * @param dispatcher The ExecutorService to use.
     * @return A new EventProvider instance.
     */
    public static EventProvider newAsynchronousEventProvider(ExecutorService dispatcher) {
        return new AsynchronousEventProvider(dispatcher);
    }
    
    
    
    /**
     * Create a new {@link EventProvider} which dispatches all events in the AWT event 
     * thread and waits (blocks current thread) after dispatching until all listeners
     * have been notified.
     * 
     * @return A new EventProvider instance.
     */
    public static EventProvider newWaitingAWTEventProvider() {
        return new AWTEventProvider(true);
    }
    
    
    
    /**
     * Creates a new {@link EventProvider} which dispatches all events in the AWT event
     * thread. Dispatching with this EventProvider will return immediately and the
     * {@link Dispatchable} will be scheduled to be run later by the AWT event thread.
     * 
     * @return A new EventProvider instance.
     */
    public static EventProvider newAsynchronousAWTEventProvider() {
        return new AWTEventProvider(false);
    }
    
    
    
    /** This class is not instantiable */
    private EventProviders() {}
}
