package polly.rx.core.orion;


public interface UniverseFactory {

    public WormholeProvider getWormholeProvider();
    
    public QuadrantProvider getQuadrantProvider();
    
    public QuadrantUpdater getQuadrantUpdater();
    
    public PortalProvider getPortalProvider();
}
