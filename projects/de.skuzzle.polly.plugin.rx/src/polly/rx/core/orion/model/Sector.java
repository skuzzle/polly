package polly.rx.core.orion.model;

import java.util.Collection;
import java.util.Date;

import de.skuzzle.polly.tools.Equatable;

public interface Sector extends Equatable {
    
    public abstract String getQuadName();

    public abstract int getX();

    public abstract int getY();

    public abstract int getAttackerBonus();

    public abstract int getDefenderBonus();

    public abstract int getSectorGuardBonus();

    public abstract Date getDate();

    public abstract SectorType getType();

    public abstract Collection<? extends Production> getRessources();
}