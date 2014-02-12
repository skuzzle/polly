package polly.rx.core.orion.model;

import java.util.Collection;
import de.skuzzle.polly.tools.Equatable;

public interface Sector extends Equatable, OrionObject {
    
    public abstract String getQuadName();

    public abstract int getX();

    public abstract int getY();

    public abstract int getAttackerBonus();

    public abstract int getDefenderBonus();

    public abstract int getSectorGuardBonus();

    public abstract SectorType getType();

    public abstract Collection<? extends Production> getRessources();
}