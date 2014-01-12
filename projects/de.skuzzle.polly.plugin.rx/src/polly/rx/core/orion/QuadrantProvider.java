package polly.rx.core.orion;

import java.util.List;

import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;


public interface QuadrantProvider extends QuadrantListener {
    
    public List<String> getAllQuadrantNames();
    
    public List<? extends Sector> getEntryPortals();
    
    public Quadrant getQuadrant(Sector sector);
    
    public Quadrant getQuadrant(String name);
    
    public List<? extends Quadrant> getAllQuadrants();
}