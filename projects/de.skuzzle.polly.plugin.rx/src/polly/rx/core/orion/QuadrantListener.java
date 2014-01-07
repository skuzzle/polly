package polly.rx.core.orion;

import java.util.EventListener;

import de.skuzzle.polly.tools.events.Dispatch;



public interface QuadrantListener extends EventListener {
    
    public static Dispatch<QuadrantListener, QuadrantEvent> QUADRANT_DELETED = 
            new Dispatch<QuadrantListener, QuadrantEvent>() {
        @Override
        public void dispatch(QuadrantListener listener, QuadrantEvent event) {
            listener.quadrantDeleted(event);
        }
    };
    
    
    
    public static Dispatch<QuadrantListener, QuadrantEvent> QUADRANT_ADDED = 
            new Dispatch<QuadrantListener, QuadrantEvent>() {
        @Override
        public void dispatch(QuadrantListener listener, QuadrantEvent event) {
            listener.quadrantAdded(event);
        }
    };
    
    
    
    public static Dispatch<QuadrantListener, QuadrantEvent> SECTORS_ADDED = 
            new Dispatch<QuadrantListener, QuadrantEvent>() {
        @Override
        public void dispatch(QuadrantListener listener, QuadrantEvent event) {
            listener.sectorsAdded(event);
        }
    };
    
    
    
    public static Dispatch<QuadrantListener, QuadrantEvent> SECTORS_UPDATED = 
            new Dispatch<QuadrantListener, QuadrantEvent>() {
        @Override
        public void dispatch(QuadrantListener listener, QuadrantEvent event) {
            listener.sectorsUpdated(event);
        }
    };
    
    
    
    public void quadrantDeleted(QuadrantEvent e);
    
    public void quadrantAdded(QuadrantEvent e);
    
    public void sectorsAdded(QuadrantEvent e);
    
    public void sectorsUpdated(QuadrantEvent e);
}