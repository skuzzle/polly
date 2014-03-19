package polly.rx.core.orion;

import java.util.EventListener;


public interface PortalListener extends EventListener {   
    
    public void portalsAdded(PortalEvent e);
    
    public void portalsMoved(PortalEvent e);
    
    public void portalsRemoved(PortalEvent e);
}
