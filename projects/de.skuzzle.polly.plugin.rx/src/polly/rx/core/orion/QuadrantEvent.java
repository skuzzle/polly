package polly.rx.core.orion;

import java.util.Collection;

import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;
import de.skuzzle.polly.tools.events.Event;


public class QuadrantEvent extends Event<QuadrantUpdater> {

    private final Collection<? extends Sector> modified;
    private final Quadrant quadrant;
    
    public QuadrantEvent(QuadrantUpdater source, Quadrant quadrant, 
            Collection<? extends Sector> modified) {
        super(source);
        this.quadrant = quadrant;
        this.modified = modified;
    }

    
    
    public Quadrant getQuadrant() {
        return this.quadrant;
    }
    
    
    
    public Collection<? extends Sector> getModified() {
        return this.modified;
    }
}
