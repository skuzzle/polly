package polly.rx.core.orion.model;

import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;

public class DefaultShipStats implements ShipStats {

    private final int aw;
    private final int shields;
    private final int pz;
    private final int str;
    private final int minC;
    private final int maxC;



    public DefaultShipStats(int aw, int shields, int pz, int str, int minC, int maxC) {
        this.aw = aw;
        this.shields = shields;
        this.pz = pz;
        this.str = str;
        this.minC = minC;
        this.maxC = maxC;
    }



    public DefaultShipStats(ShipStats stats) {
        this(stats.getAw(), stats.getShields(), stats.getPz(), stats.getStructure(),
                stats.getMinCrew(), stats.getMaxCrew());
    }



    @Override
    public String toString() {
        return OrionObjectUtil.statsString(this);
    }



    @Override
    public int hashCode() {
        return OrionObjectUtil.statsHash(this);
    }



    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return ShipStats.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return OrionObjectUtil.statsEqual(this, (ShipStats) o);
    }



    @Override
    public int getAw() {
        return this.aw;
    }



    @Override
    public int getShields() {
        return this.shields;
    }



    @Override
    public int getPz() {
        return this.pz;
    }



    @Override
    public int getStructure() {
        return this.str;
    }



    @Override
    public int getMinCrew() {
        return this.minC;
    }



    @Override
    public int getMaxCrew() {
        return this.maxC;
    }
}
