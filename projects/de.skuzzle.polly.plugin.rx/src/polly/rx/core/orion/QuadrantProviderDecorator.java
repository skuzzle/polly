package polly.rx.core.orion;

import java.util.Collection;

import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;


public class QuadrantProviderDecorator implements QuadrantProvider {

    private final QuadrantProvider wrapped;
    
    public QuadrantProviderDecorator(QuadrantProvider wrapped) {
        this.wrapped = wrapped;
    }
    
    @Override
    public Collection<String> getAllQuadrantNames() {
        return this.wrapped.getAllQuadrantNames();
    }

    @Override
    public Quadrant getQuadrant(Sector sector) {
        return this.wrapped.getQuadrant(sector);
    }

    @Override
    public Quadrant getQuadrant(String name) {
        return this.wrapped.getQuadrant(name);
    }

    @Override
    public Collection<? extends Quadrant> getAllQuadrants() {
        return this.wrapped.getAllQuadrants();
    }

    @Override
    public Collection<? extends Sector> getEntryPortals() {
        return this.wrapped.getEntryPortals();
    }

    @Override
    public void quadrantDeleted(QuadrantEvent e) {
        this.wrapped.quadrantDeleted(e);
    }

    @Override
    public void quadrantAdded(QuadrantEvent e) {
        this.wrapped.quadrantAdded(e);
    }

    @Override
    public void sectorsAdded(QuadrantEvent e) {
        this.wrapped.sectorsAdded(e);
    }

    @Override
    public void sectorsUpdated(QuadrantEvent e) {
        this.sectorsUpdated(e);
    }
}
