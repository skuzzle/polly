package polly.rx.core.orion;

import polly.rx.core.orion.pathplanning.PathPlanner;


public enum Orion implements UniverseFactory {
    INSTANCE;
    
    private QuadrantProvider quadProvider;
    private WormholeProvider holeProvider;
    private QuadrantUpdater quadUpdater;
    private PathPlanner planner;
    
    
    
    public static void initialize(
            QuadrantProvider quadProvider,
            QuadrantUpdater quadUpdater,
            WormholeProvider holeProvider) {
        
        if (INSTANCE.quadProvider != null) {
            throw new IllegalStateException("already initialized"); //$NON-NLS-1$
        } else if (quadProvider == null) {
            throw new NullPointerException(QuadrantProvider.class.getSimpleName());
        } else if (holeProvider == null) {
            throw new  NullPointerException(WormholeProvider.class.getSimpleName());
        } else if (quadUpdater == null) {
            throw new NullPointerException(QuadrantUpdater.class.getSimpleName());
        }
        
        INSTANCE.quadProvider = quadProvider;
        INSTANCE.holeProvider = holeProvider;
        INSTANCE.quadUpdater = quadUpdater;
        INSTANCE.planner = new PathPlanner(quadProvider, holeProvider);
    }
    
    
    
    private final boolean checkInitialized() {
        if (this.quadProvider == null) {
            throw new IllegalStateException("not initialized"); //$NON-NLS-1$
        }
        return this.quadProvider != null;
    }


    
    @Override
    public WormholeProvider createWormholeProvider() {
        assert this.checkInitialized();
        return this.holeProvider;
    }

    

    @Override
    public QuadrantProvider createQuadrantProvider() {
        assert this.checkInitialized();
        return this.quadProvider;
    }


    
    @Override
    public QuadrantUpdater createQuadrantUpdater() {
        assert this.checkInitialized();
        return this.quadUpdater;
    }
    
    
    
    public PathPlanner getPathPlanner() {
        assert this.checkInitialized();
        return this.planner;
    }
}
