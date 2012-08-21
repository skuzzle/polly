package de.skuzzle.polly.tools.events;

import java.util.EventListener;
import java.util.List;


/**
 * 
 * @author Simon
 * @version 27.07.2011 ae73250
 */
public interface EventProvider {
    
    public <T extends EventListener> void addListener(Class<T> listenerClass, 
            T listener);
    
    public <T extends EventListener> void removeListener(Class<T> listenerClass, 
            T listener);

    public <T extends EventListener> List<T> getListeners(Class<T> listenerClass);

    public void dispatchEvent(Dispatchable<?, ?> d);
    
    public boolean canDispatch();
    
    public void dispose();
}