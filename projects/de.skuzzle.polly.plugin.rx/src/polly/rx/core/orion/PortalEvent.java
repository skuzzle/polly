package polly.rx.core.orion;

import java.util.List;

import polly.rx.core.orion.model.Portal;
import de.skuzzle.polly.tools.events.Event;

public class PortalEvent extends Event<PortalUpdater> {

    private final String reporter;
    private final List<Portal> portals;



    public PortalEvent(PortalUpdater source, String reporter, List<Portal> portals) {
        super(source);
        this.reporter = reporter;
        this.portals = portals;
    }
    
    
    
    
    public String getReporter() {
        return this.reporter;
    }



    public List<Portal> getPortals() {
        return this.portals;
    }
}
