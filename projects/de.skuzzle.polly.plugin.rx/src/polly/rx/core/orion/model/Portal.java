package polly.rx.core.orion.model;

import de.skuzzle.polly.tools.Equatable;


public interface Portal extends Equatable, VenadOwner, OrionObject {
    
    public Sector getSector();
    
    public PortalType getType();
}
