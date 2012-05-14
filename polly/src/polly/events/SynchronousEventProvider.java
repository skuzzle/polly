package polly.events;



public class SynchronousEventProvider extends AbstractEventProvider {

    @Override
    public void dispatchEvent(Dispatchable<?, ?> d) {
        d.run();
    }
    
    

    @Override
    protected void actualDispose() {}

}
