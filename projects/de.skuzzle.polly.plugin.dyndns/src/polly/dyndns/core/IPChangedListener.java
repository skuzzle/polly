package polly.dyndns.core;

import java.util.EventListener;

import de.skuzzle.polly.tools.events.Dispatch;


public interface IPChangedListener extends EventListener {

    public final static Dispatch<IPChangedListener, IPChangedEvent> IP_CHANGED = 
            new Dispatch<IPChangedListener, IPChangedEvent>() {

        @Override
        public void dispatch(IPChangedListener listener, IPChangedEvent event) {
            listener.ipChanged(event);
        }
    };
    
    
    
    public void ipChanged(IPChangedEvent e);
}
