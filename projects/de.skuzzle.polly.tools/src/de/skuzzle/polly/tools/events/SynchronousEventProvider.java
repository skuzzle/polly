package de.skuzzle.polly.tools.events;

import java.util.EventListener;



class SynchronousEventProvider extends AbstractEventProvider {

    @Override
    @Deprecated
    public void dispatchEvent(Dispatchable<?, ?> d) {
        d.run();
    }
    
    
    
    @Override
    public <L extends EventListener, E extends Event<?>> void dispatchEvent(
            Class<L> listenerClass, E event, Dispatch<L, E> d) {
        this.notifyListeners(listenerClass, event, d);
    }
    
    
    
    @Override
    public boolean canDispatch() {
        return true;
    }
    
    

    @Override
    public void dispose() {}
}
