package polly.rx.core.orion;

import java.util.EventListener;


public interface FleetListener extends EventListener {
    
    public void ownFleetsUpdated(FleetEvent e);
    
    public void fleetsUpdated(FleetEvent e);
}
