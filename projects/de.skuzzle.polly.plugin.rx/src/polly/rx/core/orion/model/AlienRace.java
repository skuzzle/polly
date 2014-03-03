package polly.rx.core.orion.model;

import de.skuzzle.polly.tools.Equatable;


public interface AlienRace extends Equatable {

    public String getName();
    
    public String getSubName();
    
    public boolean isAggressive();
}
