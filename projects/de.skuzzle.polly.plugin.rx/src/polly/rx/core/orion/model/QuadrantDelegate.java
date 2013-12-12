package polly.rx.core.orion.model;

import java.util.Collection;

import de.skuzzle.polly.tools.Equatable;


public class QuadrantDelegate implements Quadrant {
    
    private final Quadrant wrapped;
    
    
    public QuadrantDelegate(Quadrant wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Class<?> getEquivalenceClass() {
        return this.wrapped.getEquivalenceClass();
    }

    @Override
    public boolean actualEquals(Equatable o) {
        return this.wrapped.actualEquals(o);
    }

    @Override
    public String getName() {
        return this.wrapped.getName();
    }

    @Override
    public Sector getSector(int x, int y) {
        return this.wrapped.getSector(x, y);
    }

    @Override
    public Collection<Sector> getSectors() {
        return this.wrapped.getSectors();
    }

    @Override
    public int getMaxX() {
        return this.wrapped.getMaxX();
    }

    @Override
    public int getMaxY() {
        return this.wrapped.getMaxY();
    }
}