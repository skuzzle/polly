package polly.rx.core.orion.model;

import de.skuzzle.polly.tools.Equatable;


public interface Fleet extends Equatable, VenadOwner, OrionObject {

    public int getRevorixId();
    
    public String getName();
    
    public Sector getSector();
}
