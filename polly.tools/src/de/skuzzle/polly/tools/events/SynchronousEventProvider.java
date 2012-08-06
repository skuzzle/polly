package de.skuzzle.polly.tools.events;



public class SynchronousEventProvider extends AbstractEventProvider {

    @Override
    public void dispatchEvent(Dispatchable<?, ?> d) {
        d.run();
    }
    
    

    @Override
    public void dispose() {}

}
