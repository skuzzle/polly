package polly.rx.core.orion;

import java.util.List;

import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.Wormhole;


public class WormholeProviderDecorator implements WormholeProvider {
    private final WormholeProvider wrapped;

    
    public WormholeProviderDecorator(WormholeProvider wrapped) {
        this.wrapped = wrapped;
    }
    
    @Override
    public List<Wormhole> getWormholes(Quadrant quadrant, QuadrantProvider quads) {
        return this.wrapped.getWormholes(quadrant, quads);
    }

    @Override
    public List<Wormhole> getWormholes(Sector sector, QuadrantProvider quads) {
        return this.wrapped.getWormholes(sector, quads);
    }
}
