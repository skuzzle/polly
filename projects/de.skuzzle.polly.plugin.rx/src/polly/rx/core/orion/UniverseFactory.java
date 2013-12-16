package polly.rx.core.orion;


public interface UniverseFactory {

    public WormholeProvider createWormholeProvider();
    
    public QuadrantProvider createQuadrantProvider();
    
    public QuadrantUpdater createQuadrantUpdater();
}
