package polly.rx.core.orion.model;

import java.util.Date;

import de.skuzzle.polly.tools.Equatable;


public interface Portal extends Equatable {
    
    public String getOwner();
    
    public String getOwnerClan();
    
    public PortalType getType();

    public Date getDate();
}
