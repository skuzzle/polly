package polly.rx.core.orion.model;

import de.skuzzle.polly.tools.Equatable;

public interface Wormhole extends Equatable, OrionObject {

    public abstract String getName();

    public abstract int getMinUnload();

    public abstract int getMaxUnload();

    public abstract Sector getTarget();

    public abstract Sector getSource();

    public abstract LoadRequired requiresLoad();
}