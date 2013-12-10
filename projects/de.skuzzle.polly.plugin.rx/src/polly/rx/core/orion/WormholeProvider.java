package polly.rx.core.orion;

import java.util.List;

import de.skuzzle.polly.sdk.PersistenceManagerV2;
import polly.rx.entities.QuadSector;


public interface WormholeProvider {

    public List<Wormhole> getWormholesFrom(QuadSector sector, PersistenceManagerV2 persistence);
}
