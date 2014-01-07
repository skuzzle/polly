package polly.rx.core.orion;

import java.util.Collection;

import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;


public interface QuadrantUpdater {
    
    public void deleteQuadrant(String quadName) throws OrionException;
    
    public void deleteQuadrant(Quadrant quadrant) throws OrionException;

    public void updateSectorInformation(Collection<Sector> sectors) throws OrionException;
}
