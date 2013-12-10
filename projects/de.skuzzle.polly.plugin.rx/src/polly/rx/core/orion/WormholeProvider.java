package polly.rx.core.orion;

import java.util.List;

import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.Wormhole;



public interface WormholeProvider {

    public List<Wormhole> getWormholes(Sector sector, QuadrantProvider quads);
}
