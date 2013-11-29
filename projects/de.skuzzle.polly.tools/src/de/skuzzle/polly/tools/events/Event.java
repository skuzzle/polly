package de.skuzzle.polly.tools.events;


/**
 * <p>This class is the base of all events that can be fired. It holds the source of the 
 * event and provides methods to stop delegation to further listeners if this event has 
 * been handled by one listener.</p>
 * 
 * <p>Events are used in conjunction with the {@link EventProvider} and an instance of
 * {@link Dispatch}. A Dispatch instance is used by an EventProvider and
 * serves for notifying a single listener with a certain event. The EventProvider will 
 * stop notifying further listeners as soon as one listener sets this class' 
 * {@link #isHandled()} to <code>true</code>.</p>
 * 
 * @author Simon
 * @param <T> Type of the source of this event.
 */
public class Event<T> {

    private final T source;
    private boolean isHandled;
    
    
    
    /**
     * Creates a new Event.
     * 
     * @param source The source of this event.
     * @param isHandled Whether this event was handled. If this is <code>true</code>, no
     *          listener will be notified for this event.
     */
    public Event(T source, boolean isHandled) {
        this.source = source;
        this.isHandled = isHandled;
    }
    
    
    
    /**
     * Creates a new unhandled event with a given source.
     * 
     * @param source The source of this event.
     */
    public Event(T source) {
        this(source, false);
    }
    
    
    
    /**
     * Gets the source of this event.
     * 
     * @return The source of this event.
     */
    public T getSource() {
        return this.source;
    }
    
    
    
    /**
     * Gets whether this was already handled. If this returns <code>true</code>, no 
     * further listeners will be notified about this event.
     * 
     * @return Whether this event was handled.
     */
    public boolean isHandled() {
        return this.isHandled;
    }
    
    
    
    /**
     * Sets whether this event was already handled.
     * 
     * @param isHandled Whether this event was handled.
     */
    public void setHandled(boolean isHandled) {
        this.isHandled = isHandled;
    }
}