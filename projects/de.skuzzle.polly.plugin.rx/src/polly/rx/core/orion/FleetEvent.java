package polly.rx.core.orion;

import java.util.Collection;

import polly.rx.core.orion.model.Fleet;
import de.skuzzle.polly.tools.events.Event;


public class FleetEvent extends Event<FleetTracker> {

    private final String reporter;
    private final Collection<? extends Fleet> fleets;
    
    public FleetEvent(FleetTracker source, String reporter, 
            Collection<? extends Fleet> fleets) {
        super(source);
        this.reporter = reporter;
        this.fleets = fleets;
    }

    
    
    
    public String getReporter() {
        return this.reporter;
    }
    
    
    
    public Collection<? extends Fleet> getFleets() {
        return this.fleets;
    }
}
