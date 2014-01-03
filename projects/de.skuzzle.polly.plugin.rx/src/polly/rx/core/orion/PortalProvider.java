package polly.rx.core.orion;

import java.util.List;

import polly.rx.core.orion.model.Portal;
import polly.rx.entities.DBSector;


public interface PortalProvider {

    public List<Portal> getPortals(DBSector sector);
}