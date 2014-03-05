package polly.rx.core.orion;

import java.util.List;

import polly.rx.core.orion.model.Portal;
import polly.rx.core.orion.model.PortalType;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;


public interface PortalProvider {

    public List<? extends Portal> getPortals(Sector sector);
    
    public List<? extends Portal> getPortals(Sector sector, PortalType type);
    
    public Portal getPersonalPortal(String ownerName);
    
    public Portal getClanPortal(String nameOrTag);

    public List<? extends Portal> getPortals(Quadrant quadrant, PortalType type);
    
    public List<? extends Portal> getAllPortals();
}