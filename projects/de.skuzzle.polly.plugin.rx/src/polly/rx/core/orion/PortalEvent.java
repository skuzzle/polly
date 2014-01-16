package polly.rx.core.orion;

import java.util.List;

import polly.rx.core.orion.model.Portal;
import de.skuzzle.polly.tools.events.Event;

public class PortalEvent extends Event<PortalUpdater> {

    private final List<Portal> portals;



    public PortalEvent(PortalUpdater source, List<Portal> portals) {
        super(source);
        this.portals = portals;
    }



    public List<Portal> getPortals() {
        return this.portals;
    }
}
