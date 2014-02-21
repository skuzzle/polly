package polly.rx.core.orion.model;

import de.skuzzle.polly.tools.Equatable;


public interface ShipStats extends Equatable {

    public int getAw();
    
    public int getShields();
    
    public int getPz();
    
    public int getStructure();
    
    public int getMinCrew();
    
    public int getMaxCrew();
}