package polly.rx.core.orion;

import java.util.Collection;

import polly.rx.core.orion.model.Portal;
import polly.rx.core.orion.model.Sector;


public interface PortalUpdater {

    public Collection<? extends Portal> updatePortals(Sector sector,
            Collection<?extends Portal> portals) throws OrionException;
}