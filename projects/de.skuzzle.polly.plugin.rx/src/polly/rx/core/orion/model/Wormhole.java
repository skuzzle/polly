package polly.rx.core.orion.model;

import java.util.Date;

public interface Wormhole {

    public abstract String getName();

    public abstract Date getDate();

    public abstract int getMinUnload();

    public abstract int getMaxUnload();

    public abstract Sector getTarget();

    public abstract Sector getSource();

    public abstract LoadRequired requiresLoad();
}