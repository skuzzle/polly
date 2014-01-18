package polly.rx.core.orion.model;

import java.util.Date;

import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;

public class DefaultWormhole implements Wormhole {

    private final DefaultSector source;
    private final DefaultSector target;
    private final String name;
    private final int minUnload;
    private final int maxUnload;
    private final LoadRequired load;
    private final Date date;



    public DefaultWormhole(Sector source, Sector target, String name, int minUnload,
            int maxUnload, LoadRequired load) {
        Check.objects(source, target, name, load).notNull();
        this.source = new DefaultSector(source);
        this.target = new DefaultSector(target);
        this.name = name;
        this.minUnload = minUnload;
        this.maxUnload = maxUnload;
        this.load = load;
        this.date = Time.currentTime();
    }



    public DefaultWormhole(Wormhole w) {
        this(w.getSource(), w.getTarget(), w.getName(), w.getMinUnload(), w
                .getMaxUnload(), w.requiresLoad());
    }



    @Override
    public String toString() {
        return OrionObjectUtil.wormholeString(this);
    }



    @Override
    public int hashCode() {
        return OrionObjectUtil.wormholeHash(this);
    }



    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return Wormhole.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return OrionObjectUtil.wormholesEquals(this, (Wormhole) o);
    }



    @Override
    public String getName() {
        return this.name;
    }



    @Override
    public Date getDate() {
        return this.date;
    }



    @Override
    public int getMinUnload() {
        return this.minUnload;
    }



    @Override
    public int getMaxUnload() {
        return this.maxUnload;
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
        return this.load;
    }
}
