package polly.rx.core.orion;

import java.util.Collection;

import de.skuzzle.jeve.Event;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;

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
