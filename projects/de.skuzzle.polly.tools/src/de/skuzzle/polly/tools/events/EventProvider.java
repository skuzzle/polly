package de.skuzzle.polly.tools.events;

import java.util.EventListener;
import java.util.function.BiConsumer;


/**
 * EventProviders are used to fire swing-style events. Listeners can be registered and
 * removed for a certain event. Furthermore the strategy on how to actually dispatch
 * fired event is implementation dependent. You can obtain different implementations
 * using the static factory methods in {@link EventProviders}.
 * 
 * @author Simon
 */
public interface EventProvider extends AutoCloseable {
    
    /**
     * Adds a listener which will be notified for every event represented by the
     * given listener class.
     * 
     * @param listenerClass The class representing the event(s) to listen on.
     * @param listener The listener to add.
     */
    public <T extends EventListener> void addListener(Class<T> listenerClass, 
            T listener);
    
    /**
     * Removes a listener. It will only be removed for the specified listener class and
     * can thus still be registered with this event provider if it was added for
     * further listener classes. The listener will no longer receive events represented
     * by the given listener class.
     *  
     * @param listenerClass The class representing the event(s) for which the listener
     *          should be removed.
     * @param listener The listener to remove.
     */
    public <T extends EventListener> void removeListener(Class<T> listenerClass, 
            T listener);
    
    /**
     * Gets all listeners that have been registered using 
     * {@link #addListener(Class, EventListener)} for the given listener class.
     * 
     * @param listenerClass The class representing the event for which the listeners
     *          should be retrieved.
     * @return A collection of listeners that should be notified about the event 
     *          represented by the given listener class.
     */
    public <T extends EventListener> Listeners<T> getListeners(Class<T> listenerClass);

    /**
     * Executes the given {@link Dispatchable} which causes all listeners registered with
     * that <code>Dispatchable</code> to be notified about a certain event. The strategy
     * how to run <code>Dispatchables</code> is implementation dependent. So it might for
     * example, be run in a different thread.
     * 
     * @param d The Dispatchable to execute.
     * @deprecated Use {@link #dispatchEvent(Class, Event, Dispatch)} instead
     */
    @Deprecated
    public void dispatchEvent(Dispatchable<?, ?> d);
    
    /**
     * Notifies all listeners of a certain kind about an occurred event. Consider an 
     * <tt>UserListener</tt> interface:
     * <pre>
     * public interface UserListener {
     *     public void userAdded(UserEvent e);
     *     
     *     public void userDeleted(UserEvent e);
     * }
     * </pre>
     * 
     * Notifying all registered UserListeners about an added user is as easy as calling
     * <pre>
     * eventProvider.dispatchEvent(UserListener.class, event, UserListener::userAdded)
     * </pre>
     * 
     * @param listenerClass The kind of listeners to notify.
     * @param event The occurred event which shall be passed to each listener.
     * @param bc Function to delegate the event to the specific callback method of the 
     *          listener.
     */
    public <L extends EventListener, E extends Event<?>> void dispatchEvent(
            Class<L> listenerClass, E event, BiConsumer<L, E> bc);
    
    /**
     * Gets whether this EventProvider is ready for dispatching.
     * 
     * @return Whether further events can be dispatched using 
     *          {@link #dispatchEvent(Dispatchable)}
     */
    public boolean canDispatch();
    
    /**
     * Closes this EventProvider. Depending on its implementation, it might not be 
     * able to dispatch further events after disposing.
     */
    public void dispose();
    
    @Override
    public default void close() {
        dispose();
    }
}