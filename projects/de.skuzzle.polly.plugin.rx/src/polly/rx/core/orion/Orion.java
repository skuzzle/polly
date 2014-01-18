package polly.rx.core.orion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polly.rx.MyPlugin;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.pathplanning.PathPlanner;
import polly.rx.parsing.ParseException;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.ListType;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.User;

public enum Orion implements UniverseFactory {
    INSTANCE;

    private QuadrantProvider quadProvider;
    private WormholeProvider holeProvider;
    private QuadrantUpdater quadUpdater;
    private PortalProvider portalProvider;
    private PortalUpdater portalUpdater;
    private FleetTracker fleetTracker;
    private PathPlanner planner;



    public static void initialize(QuadrantProvider quadProvider,
            QuadrantUpdater quadUpdater, WormholeProvider holeProvider,
            PortalProvider portalProvider, PortalUpdater portalUpdater, 
            FleetTracker fleetTracker) {

        if (INSTANCE.quadProvider != null) {
            throw new IllegalStateException("already initialized"); //$NON-NLS-1$
        } else if (quadProvider == null) {
            throw new NullPointerException(QuadrantProvider.class.getSimpleName());
        } else if (holeProvider == null) {
            throw new NullPointerException(WormholeProvider.class.getSimpleName());
        } else if (quadUpdater == null) {
            throw new NullPointerException(QuadrantUpdater.class.getSimpleName());
        } else if (portalProvider == null) {
            throw new NullPointerException(PortalProvider.class.getSimpleName());
        } else if (portalUpdater == null) {
            throw new NullPointerException(PortalUpdater.class.getSimpleName());
        } else if (fleetTracker == null) {
            throw new NullPointerException(FleetTracker.class.getSimpleName());
        }

        INSTANCE.quadProvider = quadProvider;
        INSTANCE.holeProvider = holeProvider;
        INSTANCE.quadUpdater = quadUpdater;
        INSTANCE.portalProvider = portalProvider;
        INSTANCE.portalUpdater = portalUpdater;
        INSTANCE.fleetTracker = fleetTracker;
        INSTANCE.planner = new PathPlanner(quadProvider, holeProvider);
    }



    private final boolean checkInitialized() {
        if (this.quadProvider == null) {
            throw new IllegalStateException("not initialized"); //$NON-NLS-1$
        }
        return this.quadProvider != null;
    }



    @Override
    public WormholeProvider getWormholeProvider() {
        assert this.checkInitialized();
        return this.holeProvider;
    }



    @Override
    public QuadrantProvider getQuadrantProvider() {
        assert this.checkInitialized();
        return this.quadProvider;
    }



    @Override
    public QuadrantUpdater getQuadrantUpdater() {
        assert this.checkInitialized();
        return this.quadUpdater;
    }



    @Override
    public PortalProvider getPortalProvider() {
        assert this.checkInitialized();
        return this.portalProvider;
    }


    
    public PortalUpdater getPortalUpdater() {
        return this.portalUpdater;
    }
    

    
    public PathPlanner getPathPlanner() {
        assert this.checkInitialized();
        return this.planner;
    }
    
    
    
    
    public FleetTracker getFleetTracker() {
        assert this.checkInitialized();
        return this.fleetTracker;
    }



    public List<Sector> getPersonalPortals(User user) {
        if (user == null) {
            return Collections.emptyList();
        }
        final Types portals = user.getAttribute(MyPlugin.PORTALS);
        if (portals instanceof ListType) {
            final ListType lt = (ListType) portals;
            final List<Sector> result = new ArrayList<>(lt.getElements().size());
            for (final Types t : lt.getElements()) {
                final StringType st = (StringType) t;
                try {
                    final Sector s = QuadrantUtils.parse(st.getValue());
                    result.add(s);
                } catch (ParseException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
            return result;
        }
        return Collections.emptyList();
    }
}
