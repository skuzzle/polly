package polly.rx.core.orion;

import java.util.EventListener;

import de.skuzzle.polly.tools.events.Dispatch;


public interface PortalListener extends EventListener {

    public final static Dispatch<PortalListener, PortalEvent> PORTALS_ADDED = 
            new Dispatch<PortalListener, PortalEvent>() {
        @Override
        public void dispatch(PortalListener listener, PortalEvent event) {
            listener.portalsAdded(event);
        }
    };
    
    public final static Dispatch<PortalListener, PortalEvent> PORTALS_MOVED = 
            new Dispatch<PortalListener, PortalEvent>() {
        @Override
        public void dispatch(PortalListener listener, PortalEvent event) {
            listener.portalsMoved(event);
        }
    };
    
    public final static Dispatch<PortalListener, PortalEvent> PORTALS_REMOVED = 
            new Dispatch<PortalListener, PortalEvent>() {
        @Override
        public void dispatch(PortalListener listener, PortalEvent event) {
            listener.portalsRemoved(event);
        }
    };
    
    
    
    public void portalsAdded(PortalEvent e);
    
    public void portalsMoved(PortalEvent e);
    
    public void portalsRemoved(PortalEvent e);
}
