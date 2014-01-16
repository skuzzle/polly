package polly.rx.core.orion;

import java.util.Collection;

import polly.rx.core.orion.model.Portal;
import polly.rx.core.orion.model.Sector;


public interface PortalUpdater {
    
    public void addPortalListener(PortalListener listener);
    
    public void removePortalListener(PortalListener listener);

    public Collection<? extends Portal> updatePortals(Sector sector,
            Collection<?extends Portal> portals) throws OrionException;
}