package polly.rx.core.orion;

import java.util.Collection;

import polly.rx.core.orion.model.Portal;


public interface PortalUpdater {

    public Collection<? extends Portal> updatePortals(
            Collection<?extends Portal> portals) throws OrionException;
}