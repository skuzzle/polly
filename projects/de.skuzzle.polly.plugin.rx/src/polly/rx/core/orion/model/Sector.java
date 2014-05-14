package polly.rx.core.orion.model;

import java.util.Collection;
import java.util.Comparator;

import de.skuzzle.polly.tools.Equatable;

public interface Sector extends Equatable, OrionObject, Comparable<Sector> {
    
    public final static Comparator<Sector> SECTOR_COMPERATOR = 
            Comparator.comparing(Sector::getQuadName)
                .thenComparing(Sector::getX)
                .thenComparing(Sector::getY);
    
    public abstract String getQuadName();

    public abstract int getX();

    public abstract int getY();

    public abstract int getAttackerBonus();

    public abstract int getDefenderBonus();

    public abstract int getSectorGuardBonus();

    public abstract SectorType getType();

    public abstract Collection<? extends Production> getRessources();
}