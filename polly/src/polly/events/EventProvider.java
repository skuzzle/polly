package polly.events;

import java.util.EventListener;
import java.util.List;

public interface EventProvider {
    
    public <T extends EventListener> void addListener(Class<T> listenerClass, 
            T listener);
    
    public <T extends EventListener> void removeListener(Class<T> listenerClass, 
            T listener);

    public <T extends EventListener> List<T> getListeners(Class<T> listenerClass);

    public void dispatchEvent(Runnable r);
    
    public void dispose();
}