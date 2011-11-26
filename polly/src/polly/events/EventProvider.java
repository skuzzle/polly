package polly.events;

import java.util.EventListener;
import java.util.List;

import de.skuzzle.polly.sdk.Disposable;

/**
 * 
 * @author Simon
 * @version 27.07.2011 ae73250
 */
public interface EventProvider extends Disposable {
    
    public <T extends EventListener> void addListener(Class<T> listenerClass, 
            T listener);
    
    public <T extends EventListener> void removeListener(Class<T> listenerClass, 
            T listener);

    public <T extends EventListener> List<T> getListeners(Class<T> listenerClass);

    public void dispatchEvent(Dispatchable<?, ?> d);
}