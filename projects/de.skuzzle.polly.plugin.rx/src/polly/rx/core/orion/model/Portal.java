package polly.rx.core.orion.model;

import java.util.Date;

import de.skuzzle.polly.tools.Equatable;


public interface Portal extends Equatable, VenadOwner {
    
    public Sector getSector();
    
    public PortalType getType();

    public Date getDate();
}
