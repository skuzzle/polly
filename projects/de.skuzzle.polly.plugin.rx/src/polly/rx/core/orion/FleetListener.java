package polly.rx.core.orion;

import de.skuzzle.jeve.Listener;


public interface FleetListener extends Listener {
    
    public void ownFleetsUpdated(FleetEvent e);
    
    public void fleetsUpdated(FleetEvent e);
}
