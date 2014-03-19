package polly.rx.core.orion;

import java.util.EventListener;



public interface QuadrantListener extends EventListener {

    public void quadrantDeleted(QuadrantEvent e);
    
    public void quadrantAdded(QuadrantEvent e);
    
    public void sectorsAdded(QuadrantEvent e);
    
    public void sectorsUpdated(QuadrantEvent e);
}