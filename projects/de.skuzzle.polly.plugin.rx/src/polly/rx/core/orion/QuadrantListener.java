package polly.rx.core.orion;

import de.skuzzle.jeve.Listener;



public interface QuadrantListener extends Listener {

    public void quadrantDeleted(QuadrantEvent e);
    
    public void quadrantAdded(QuadrantEvent e);
    
    public void sectorsAdded(QuadrantEvent e);
    
    public void sectorsUpdated(QuadrantEvent e);
}