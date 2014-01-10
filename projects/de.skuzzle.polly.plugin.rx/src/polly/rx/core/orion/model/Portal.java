package polly.rx.core.orion.model;

import java.util.Date;

import de.skuzzle.polly.tools.Equatable;


public interface Portal extends Equatable {
    
    public Sector getSector();
    
    public String getOwner();
    
    public String getOwnerClan();
    
    public PortalType getType();

    public Date getDate();
}
