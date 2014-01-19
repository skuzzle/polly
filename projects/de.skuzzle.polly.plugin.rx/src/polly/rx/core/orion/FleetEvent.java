package polly.rx.core.orion;

import java.util.Collection;

import polly.rx.core.orion.model.Fleet;
import de.skuzzle.polly.tools.events.Event;


public class FleetEvent extends Event<FleetTracker> {

    private final Collection<? extends Fleet> fleets;
    
    public FleetEvent(FleetTracker source, Collection<? extends Fleet> fleets) {
        super(source);
        this.fleets = fleets;
    }

    
    
    
    public Collection<? extends Fleet> getFleets() {
        return this.fleets;
    }
}
