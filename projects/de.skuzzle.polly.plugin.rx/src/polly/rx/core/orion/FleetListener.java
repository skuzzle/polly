package polly.rx.core.orion;

import java.util.EventListener;

import de.skuzzle.polly.tools.events.Dispatch;


public interface FleetListener extends EventListener {
    
    public final static Dispatch<FleetListener, FleetEvent> OWN_FLEETS_UPDATED = 
            new Dispatch<FleetListener, FleetEvent>() {
        @Override
        public void dispatch(FleetListener listener, FleetEvent event) {
            listener.ownFleetsUpdated(event);
        }
    };
    
    public final static Dispatch<FleetListener, FleetEvent> FLEETS_UPDATED = 
            new Dispatch<FleetListener, FleetEvent>() {
        @Override
        public void dispatch(FleetListener listener, FleetEvent event) {
            listener.fleetsUpdated(event);
        }
    };

    
    public void ownFleetsUpdated(FleetEvent e);
    
    public void fleetsUpdated(FleetEvent e);
}
