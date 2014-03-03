package polly.rx.core.orion.model;

import de.skuzzle.polly.tools.Equatable;


public interface AlienSpawn extends Equatable {

    public String getName();
    
    public AlienRace getRace();
    
    public Sector getSector();
}
