package polly.rx.core.orion.model;

import java.util.Collection;

import de.skuzzle.polly.tools.Equatable;


public class QuadrantDecorator implements Quadrant {
    
    private final Quadrant wrapped;
    
    
    public QuadrantDecorator(Quadrant wrapped) {
        if (wrapped == null) {
            throw new NullPointerException();
        }
        this.wrapped = wrapped;
    }
    
    // compatibility to DisplayQuadrant
    public String getQuadId() {
        return this.getName().replace(" ", "_"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    @Override
    public String toString() {
        return this.wrapped.toString();
    }
    
    @Override
    public int hashCode() {
        return this.wrapped.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return this.wrapped.equals(obj);
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
    public Collection<? extends Sector> getSectors() {
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