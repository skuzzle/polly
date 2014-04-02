package de.skuzzle.polly.tools.events;


class SynchronousEventProvider extends AbstractEventProvider {

    @Override
    @Deprecated
    public void dispatchEvent(Dispatchable<?, ?> d) {
        d.run();
    }
    
    
    
    @Override
    public boolean canDispatch() {
        return true;
    }
    
    

    @Override
    public void dispose() {}
}
