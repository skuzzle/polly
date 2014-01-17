package polly.rx.core.orion.model;

import java.util.Date;

import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;

public class DefaultFleet implements Fleet {

    private final int rxId;
    private final String name;
    private final String ownerName;
    private final String ownerClan;
    private final DefaultSector sector;
    private final Date date;



    public DefaultFleet(int rxId, String name, String ownerName, String ownerClan,
            Sector sector) {
        Check.notNull(name, ownerName, ownerClan, sector);
        this.rxId = rxId;
        this.name = name;
        this.ownerName = ownerName;
        this.ownerClan = ownerClan;
        this.sector = new DefaultSector(sector);
        this.date = Time.currentTime();
    }



    public DefaultFleet(Fleet f) {
        this(f.getRevorixId(), f.getName(), f.getOwnerName(), f.getOwnerClan(), f
                .getSpottedAt());
    }



    @Override
    public int getRevorixId() {
        return this.rxId;
    }



    @Override
    public String getName() {
        return this.name;
    }



    @Override
    public String getOwnerName() {
        return this.ownerName;
    }



    @Override
    public String getOwnerClan() {
        return this.ownerClan;
    }



    @Override
    public Date getDate() {
        return this.date;
    }



    @Override
    public Sector getSpottedAt() {
        return this.sector;
    }



    @Override
    public String toString() {
        return OrionObjectUtil.fleetString(this);
    }



    @Override
    public int hashCode() {
        return OrionObjectUtil.fleetHash(this);
    }



    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return Fleet.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return OrionObjectUtil.fleetsEqual(this, (Fleet) o);
    }
}
