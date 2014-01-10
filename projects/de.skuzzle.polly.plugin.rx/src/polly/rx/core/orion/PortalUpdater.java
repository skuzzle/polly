package polly.rx.core.orion;

import polly.rx.core.orion.model.Portal;


public interface PortalUpdater {

    public Portal updatePortal(Portal newPortal) throws OrionException;
}