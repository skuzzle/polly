package polly.rx.core.orion.model;

import java.util.Collection;

import de.skuzzle.polly.tools.Equatable;

public interface Quadrant extends Equatable {

    public abstract String getName();

    public abstract Sector getSector(int x, int y);

    public abstract Collection<Sector> getSectors();

    public abstract int getMaxX();

    public abstract int getMaxY();
}