package polly.rx.core.orion;

import java.util.Collection;

import polly.rx.core.orion.model.Quadrant;


public interface QuadrantProvider {

    public Collection<String> getAllQuadrantNames();
    
    public Quadrant getQuadrant(String name);
    
    public Collection<Quadrant> getAllQuadrants();
}