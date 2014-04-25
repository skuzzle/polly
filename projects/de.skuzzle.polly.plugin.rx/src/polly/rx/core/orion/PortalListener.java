package polly.rx.core.orion;

import de.skuzzle.jeve.Listener;


public interface PortalListener extends Listener {   
    
    public void portalsAdded(PortalEvent e);
    
    public void portalsMoved(PortalEvent e);
    
    public void portalsRemoved(PortalEvent e);
}
