package polly.rx.core.orion.model;

import java.util.Date;

import de.skuzzle.polly.sdk.time.Time;


public class EntryPortalWormhole implements Wormhole {
    
    private final Sector source;
    private final Sector target;
    

    public EntryPortalWormhole(Sector source, Sector target) {
        super();
        this.source = source;
        this.target = target;
    }

    @Override
    public String getName() {
        return SectorType.EINTRITTS_PORTAL.toString();
    }

    @Override
    public Date getDate() {
        return Time.currentTime();
    }

    @Override
    public int getMinUnload() {
        return 0;
    }

    @Override
    public int getMaxUnload() {
        return 0;
    }

    @Override
    public Sector getTarget() {
        return this.target;
    }

    @Override
    public Sector getSource() {
        return this.source;
    }

    @Override
    public LoadRequired requiresLoad() {
        return LoadRequired.NONE;
    }
}
