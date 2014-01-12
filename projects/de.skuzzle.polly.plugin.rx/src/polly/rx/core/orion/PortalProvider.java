package polly.rx.core.orion;

import java.util.Collection;

import polly.rx.core.orion.model.Portal;
import polly.rx.core.orion.model.PortalType;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;


public interface PortalProvider {

    public Collection<? extends Portal> getPortals(Sector sector);
    
    public Portal getPersonalPortal(String ownerName);
    
    public Portal getClanPortal(String nameOrTag);

    Collection<? extends Portal> getPortals(Quadrant quadrant, PortalType type);
}