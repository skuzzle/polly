package polly.rx.core.orion.model;

import java.util.Date;

import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;

public class DefaultPortal implements Portal {

    private final DefaultSector sector;
    private final String ownerName;
    private final String ownerClan;
    private final PortalType type;
    private final Date date;



    public DefaultPortal(Sector sector, String ownerName, String ownerClan,
            PortalType type) {
        Check.objects(sector, ownerName, ownerClan, type).notNull();
        this.sector = new DefaultSector(sector);
        this.ownerName = ownerName;
        this.ownerClan = ownerClan;
        this.type = type;
        this.date = Time.currentTime();
    }



    public DefaultPortal(Portal p) {
        this(p.getSector(), p.getOwner(), p.getOwnerClan(), p.getType());
    }



    @Override
    public String toString() {
        return OrionObjectUtil.portalString(this);
    }



    @Override
    public int hashCode() {
        return OrionObjectUtil.portalHash(this);
    }



    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return Portal.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return OrionObjectUtil.portalsEqual(this, (Portal) o);
    }



    @Override
    public Sector getSector() {
        return this.sector;
    }



    @Override
    public String getOwner() {
        return this.ownerName;
    }



    @Override
    public String getOwnerClan() {
        return this.ownerClan;
    }



    @Override
    public PortalType getType() {
        return this.type;
    }



    @Override
    public Date getDate() {
        return this.date;
    }
}
