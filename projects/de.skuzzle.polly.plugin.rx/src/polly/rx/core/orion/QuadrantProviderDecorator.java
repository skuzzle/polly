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
    public Collection<Quadrant> getAllQuadrants() {
        return this.wrapped.getAllQuadrants();
    }
}
