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

    private ResourcePriceProvider priceProvider;
    private LoginCodeManager loginCodeManager;
    private QuadrantProvider quadProvider;
    private WormholeProvider holeProvider;
    private VenadUserMapper userMapper;
    private QuadrantUpdater quadUpdater;
    private PortalProvider portalProvider;
    private PortalUpdater portalUpdater;
    private FleetTracker fleetTracker;
    private ShipProvider shipProvider;
    private PathPlanner planner;


    public static void initialize(QuadrantProvider quadProvider,
            QuadrantUpdater quadUpdater, WormholeProvider holeProvider,
            PortalProvider portalProvider, PortalUpdater portalUpdater,
            FleetTracker fleetTracker, ResourcePriceProvider priceProvider,
            VenadUserMapper userMapper) {

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
        } else if (priceProvider == null) {
            throw new NullPointerException(ResourcePriceProvider.class.getSimpleName());
        } else if (userMapper == null) {
            throw new NullPointerException(VenadUserMapper.class.getSimpleName());
        } // TODO: ship provider

        INSTANCE.quadProvider = quadProvider;
        INSTANCE.holeProvider = holeProvider;
        INSTANCE.quadUpdater = quadUpdater;
        INSTANCE.portalProvider = portalProvider;
        INSTANCE.portalUpdater = portalUpdater;
        INSTANCE.fleetTracker = fleetTracker;
        INSTANCE.priceProvider = priceProvider;
        INSTANCE.userMapper = userMapper;
        INSTANCE.planner = new PathPlanner(quadProvider, holeProvider);
        INSTANCE.loginCodeManager = new LoginCodeManager();
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



    public ResourcePriceProvider getPriceProvider() {
        return this.priceProvider;
    }



    public ShipProvider getShipProvider() {
        this.checkInitialized();
        return this.shipProvider;
    }



    public PathPlanner getPathPlanner() {
        assert this.checkInitialized();
        return this.planner;
    }



    public FleetTracker getFleetTracker() {
        assert this.checkInitialized();
        return this.fleetTracker;
    }

    
    
    public LoginCodeManager getLoginCodeManager() {
        assert this.checkInitialized();
        return this.loginCodeManager;
    }


    
    public VenadUserMapper getUserMapper() {
        this.checkInitialized();
        return this.userMapper;
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
