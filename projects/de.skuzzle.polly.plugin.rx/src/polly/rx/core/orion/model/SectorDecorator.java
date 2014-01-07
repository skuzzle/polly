package polly.rx.core.orion.model;

import java.util.Collection;
import java.util.Date;

import de.skuzzle.polly.tools.Equatable;


public class SectorDecorator implements Sector {

    private final Sector wrapped;
    
    public SectorDecorator(Sector wrapped) {
        if (wrapped == null) {
            throw new NullPointerException();
        }
        this.wrapped = wrapped;
    }
    
    @Override
    public int hashCode() {
        return this.wrapped.hashCode();
    }
    
    @Override
    public String toString() {
        return this.wrapped.toString();
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
    public String getQuadName() {
        return this.wrapped.getQuadName();
    }

    @Override
    public int getX() {
        return this.wrapped.getX();
    }

    @Override
    public int getY() {
        return this.wrapped.getY();
    }

    @Override
    public int getAttackerBonus() {
        return this.wrapped.getAttackerBonus();
    }

    @Override
    public int getDefenderBonus() {
        return this.wrapped.getDefenderBonus();
    }

    @Override
    public int getSectorGuardBonus() {
        return this.wrapped.getSectorGuardBonus();
    }

    @Override
    public Date getDate() {
        return this.wrapped.getDate();
    }

    @Override
    public SectorType getType() {
        return this.wrapped.getType();
    }

    @Override
    public Collection<? extends Production> getRessources() {
        return this.wrapped.getRessources();
    }
}
