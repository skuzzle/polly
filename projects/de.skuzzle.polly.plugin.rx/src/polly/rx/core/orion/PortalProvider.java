package polly.rx.core.orion;

import java.util.List;

import polly.rx.entities.QuadSector;


public interface PortalProvider {

    public List<Portal> getPortals(QuadSector sector);
}